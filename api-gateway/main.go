package main

import (
	"context"
	"log"
	"net"
	"net/http"
	"net/http/httputil"
	"net/url"
	"os"
	"os/signal"
	"strings"
	"syscall"
	"time"
)

type config struct {
	addr             string
	userService      *url.URL
	adminService     *url.URL
	professorService *url.URL
	studentService   *url.URL
	allowedOrigin    []string
}

func main() {
	cfg := mustLoadConfig()

	mux := http.NewServeMux()
	mux.HandleFunc("GET /health", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte(`{"ok":true}`))
	})

	// Routes
	mux.Handle("/api/auth/", withRequestLogging(withCORS(cfg.allowedOrigin, reverseProxy(cfg.userService))))
	mux.Handle("/api/admin/", withRequestLogging(withCORS(cfg.allowedOrigin, reverseProxy(cfg.adminService))))
	mux.Handle("/api/professor/", withRequestLogging(withCORS(cfg.allowedOrigin, reverseProxy(cfg.professorService))))
	mux.Handle("/api/student/", withRequestLogging(withCORS(cfg.allowedOrigin, reverseProxy(cfg.studentService))))

	// Nice error for unknown routes (helps frontend debugging).
	mux.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusNotFound)
		_, _ = w.Write([]byte(`{"error":"route_not_found"}`))
	})

	srv := &http.Server{
		Addr:              cfg.addr,
		Handler:           mux,
		ReadHeaderTimeout: 10 * time.Second,
	}

	l, err := net.Listen("tcp", cfg.addr)
	if err != nil {
		log.Fatalf("listen %s: %v", cfg.addr, err)
	}

	log.Printf("api-gateway listening on %s", cfg.addr)
	log.Printf("proxy /api/auth/* -> %s", cfg.userService.String())
	log.Printf("proxy /api/admin/* -> %s", cfg.adminService.String())
	log.Printf("proxy /api/professor/* -> %s", cfg.professorService.String())
	log.Printf("proxy /api/student/* -> %s", cfg.studentService.String())

	go func() {
		if err := srv.Serve(l); err != nil && err != http.ErrServerClosed {
			log.Fatalf("server error: %v", err)
		}
	}()

	// Graceful shutdown.
	stop := make(chan os.Signal, 1)
	signal.Notify(stop, syscall.SIGINT, syscall.SIGTERM)
	<-stop

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	_ = srv.Shutdown(ctx)
}

func mustLoadConfig() config {
	addr := envOr("GATEWAY_ADDR", ":8080")
	userServiceURL := envOr("USER_SERVICE_URL", "http://localhost:8081")
	u, err := url.Parse(userServiceURL)
	if err != nil {
		log.Fatalf("invalid USER_SERVICE_URL %q: %v", userServiceURL, err)
	}

	adminServiceURL := envOr("ADMIN_SERVICE_URL", "http://localhost:8083")
	a, err := url.Parse(adminServiceURL)
	if err != nil {
		log.Fatalf("invalid ADMIN_SERVICE_URL %q: %v", adminServiceURL, err)
	}

	professorServiceURL := envOr("PROFESSOR_SERVICE_URL", "http://localhost:8084")
	p, err := url.Parse(professorServiceURL)
	if err != nil {
		log.Fatalf("invalid PROFESSOR_SERVICE_URL %q: %v", professorServiceURL, err)
	}

	studentServiceURL := envOr("STUDENT_SERVICE_URL", "http://localhost:8085")
	s, err := url.Parse(studentServiceURL)
	if err != nil {
		log.Fatalf("invalid STUDENT_SERVICE_URL %q: %v", studentServiceURL, err)
	}

	allowed := envOr("ALLOWED_ORIGINS", "http://localhost:5173,http://127.0.0.1:5173")
	allowedOrigins := splitCSV(allowed)

	return config{
		addr:             addr,
		userService:      u,
		adminService:     a,
		professorService: p,
		studentService:   s,
		allowedOrigin:    allowedOrigins,
	}
}

func reverseProxy(target *url.URL) http.Handler {
	p := httputil.NewSingleHostReverseProxy(target)

	// Keep original Host in a header; set upstream Host to target.Host.
	originalDirector := p.Director
	p.Director = func(r *http.Request) {
		originalHost := r.Host
		originalDirector(r)
		r.Header.Set("X-Forwarded-Host", originalHost)
		r.Header.Set("X-Forwarded-Proto", schemeOrDefault(r))
	}

	p.ErrorHandler = func(w http.ResponseWriter, r *http.Request, err error) {
		log.Printf("proxy error %s %s: %v", r.Method, r.URL.Path, err)
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusBadGateway)
		_, _ = w.Write([]byte(`{"error":"bad_gateway"}`))
	}

	// Ensure the gateway is the single source of truth for CORS.
	// If the upstream also adds CORS headers (e.g. Access-Control-Allow-Origin: *),
	// browsers will reject responses with multiple values.
	p.ModifyResponse = func(resp *http.Response) error {
		resp.Header.Del("Access-Control-Allow-Origin")
		resp.Header.Del("Access-Control-Allow-Credentials")
		resp.Header.Del("Access-Control-Allow-Headers")
		resp.Header.Del("Access-Control-Allow-Methods")
		resp.Header.Del("Access-Control-Expose-Headers")
		resp.Header.Del("Access-Control-Max-Age")
		return nil
	}

	return p
}

func withCORS(allowedOrigins []string, next http.Handler) http.Handler {
	allowAll := len(allowedOrigins) == 1 && allowedOrigins[0] == "*"
	allowedSet := map[string]struct{}{}
	for _, o := range allowedOrigins {
		if o == "" {
			continue
		}
		allowedSet[o] = struct{}{}
	}

	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		origin := r.Header.Get("Origin")
		if origin != "" && (allowAll || isAllowedOrigin(allowedSet, origin)) {
			w.Header().Set("Access-Control-Allow-Origin", origin)
			w.Header().Set("Vary", "Origin")
			w.Header().Set("Access-Control-Allow-Credentials", "true")
			w.Header().Set("Access-Control-Allow-Headers", "Authorization, Content-Type")
			w.Header().Set("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS")
		}

		if r.Method == http.MethodOptions {
			w.WriteHeader(http.StatusNoContent)
			return
		}

		next.ServeHTTP(w, r)
	})
}

func withRequestLogging(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		start := time.Now()
		next.ServeHTTP(w, r)
		log.Printf("%s %s %s", r.Method, r.URL.Path, time.Since(start).Truncate(time.Millisecond))
	})
}

func isAllowedOrigin(allowed map[string]struct{}, origin string) bool {
	_, ok := allowed[origin]
	return ok
}

func schemeOrDefault(r *http.Request) string {
	if r.TLS != nil {
		return "https"
	}
	return "http"
}

func envOr(key, def string) string {
	if v := strings.TrimSpace(os.Getenv(key)); v != "" {
		return v
	}
	return def
}

func splitCSV(s string) []string {
	parts := strings.Split(s, ",")
	out := make([]string, 0, len(parts))
	for _, p := range parts {
		p = strings.TrimSpace(p)
		if p != "" {
			out = append(out, p)
		}
	}
	if len(out) == 0 {
		return []string{"*"}
	}
	return out
}

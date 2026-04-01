# Gradify API Gateway (Go)

This service is a small reverse-proxy API gateway so the frontend talks to **one** backend entrypoint.

## Run (local)

```bash
cd api-gateway
go run .
```

Defaults:

- Gateway listens on `:8080`
- Proxies `/api/auth/*` to `http://localhost:8081` (the `user-service`)
- Allows CORS from `http://localhost:5173` (Vite dev server)

## Configuration

Set env vars:

- `GATEWAY_ADDR` (default `:8080`)
- `USER_SERVICE_URL` (default `http://localhost:8081`)
- `ALLOWED_ORIGINS` (default `http://localhost:5173`, comma-separated, or `*`)


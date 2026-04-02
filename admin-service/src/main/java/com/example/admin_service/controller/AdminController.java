package com.example.admin_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.admin_service.service.SupabaseAuthAdminService;
import com.example.admin_service.service.SupabaseAuthAdminService.SupabaseAuthException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private final SupabaseAuthAdminService supabaseAuthAdminService;

	public AdminController(SupabaseAuthAdminService supabaseAuthAdminService) {
		this.supabaseAuthAdminService = supabaseAuthAdminService;
	}

	@GetMapping("/health")
	public Map<String, String> health() {
		return Map.of("service", "admin-service", "status", "ok");
	}

	@PostMapping("/users")
	public ResponseEntity<?> createUser(@RequestBody CreateUserRequest body) {
		if (body.email() == null || body.email().isBlank()) {
			return ResponseEntity.badRequest().body(Map.of("error", "email_required"));
		}
		if (body.password() == null || body.password().isBlank()) {
			return ResponseEntity.badRequest().body(Map.of("error", "password_required"));
		}
		if (!supabaseAuthAdminService.isServiceRoleConfigured()) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
					"error", "supabase_service_role_not_configured",
					"hint", "Set supabase.service_role.key or env SUPABASE_SERVICE_ROLE_KEY (Supabase → Settings → API → service_role)."));
		}
		try {
			Map<String, Object> created = supabaseAuthAdminService.createAuthUser(body.email(), body.password());
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (SupabaseAuthException e) {
			return ResponseEntity.status(e.getStatus()).body(Map.of(
					"error", "supabase_auth_failed",
					"details", e.getResponseBody()));
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("error", e.getMessage()));
		}
	}

	public record CreateUserRequest(String email, String password) {
	}
}

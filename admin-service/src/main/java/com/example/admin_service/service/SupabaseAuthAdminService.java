package com.example.admin_service.service;

import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class SupabaseAuthAdminService {

	private final RestTemplate restTemplate;

	@Value("${supabase.url}")
	private String supabaseUrl;

	@Value("${supabase.service_role.key:}")
	private String serviceRoleKey;

	public SupabaseAuthAdminService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public boolean isServiceRoleConfigured() {
		return StringUtils.hasText(serviceRoleKey);
	}

	public void updateUserProfile(String email, String userId, String name, String role) {
		if (!isServiceRoleConfigured()) {
			throw new IllegalStateException("supabase_service_role_not_configured");
		}

		String url = buildUsersFilterUrl(email, userId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apikey", serviceRoleKey);
		headers.set("Authorization", "Bearer " + serviceRoleKey);
		headers.set("Prefer", "return=representation");

		Map<String, Object> payload = Map.of(
				"name", name,
				"role", role);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

		try {
			// Two-phase flow: create auth user first, then update mirrored public.users row.
			// Mirror row creation can be slightly delayed, so retry update briefly.
			for (int attempt = 1; attempt <= 8; attempt++) {
				ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.PATCH, entity, List.class);
				List body = response.getBody();
				if (body != null && !body.isEmpty()) {
					return;
				}

				try {
					Thread.sleep(400);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					throw new IllegalStateException("profile_update_interrupted");
				}
			}

			throw new UserProfileNotFoundException("user_record_not_found_for_email");
		} catch (HttpClientErrorException e) {
			throw new SupabaseAuthException(e.getStatusCode().value(), e.getResponseBodyAsString());
		} catch (RestClientResponseException e) {
			throw new SupabaseAuthException(e.getStatusCode().value(), e.getResponseBodyAsString());
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> listProfessors() {
		if (!isServiceRoleConfigured()) {
			throw new IllegalStateException("supabase_service_role_not_configured");
		}

		String url = supabaseUrl.replaceAll("/+$", "")
				+ "/rest/v1/users?select=id,email,name,role&role=eq.PROFESSOR&order=created_at.desc";

		HttpHeaders headers = new HttpHeaders();
		headers.set("apikey", serviceRoleKey);
		headers.set("Authorization", "Bearer " + serviceRoleKey);

		try {
			ResponseEntity<List> response = restTemplate.exchange(
					url,
					HttpMethod.GET,
					new HttpEntity<>(headers),
					List.class);

			List body = response.getBody();
			if (body == null) {
				return Collections.emptyList();
			}
			return body;
		} catch (HttpClientErrorException e) {
			throw new SupabaseAuthException(e.getStatusCode().value(), e.getResponseBodyAsString());
		} catch (RestClientResponseException e) {
			throw new SupabaseAuthException(e.getStatusCode().value(), e.getResponseBodyAsString());
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> createCourse(String professorId, String courseCode, String courseName, String createdAt) {
		if (!isServiceRoleConfigured()) {
			throw new IllegalStateException("supabase_service_role_not_configured");
		}

		String url = supabaseUrl.replaceAll("/+$", "") + "/rest/v1/courses";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apikey", serviceRoleKey);
		headers.set("Authorization", "Bearer " + serviceRoleKey);
		headers.set("Prefer", "return=representation");

		Map<String, Object> payload = Map.of(
				"professor_id", professorId,
				"course_code", courseCode,
				"course_name", courseName,
				"created_at", createdAt);

		try {
			ResponseEntity<List> response = restTemplate.postForEntity(
					url,
					new HttpEntity<>(payload, headers),
					List.class);
			List body = response.getBody();
			if (body != null && !body.isEmpty() && body.get(0) instanceof Map<?, ?> first) {
				return (Map<String, Object>) first;
			}
			return Map.of("created", true);
		} catch (HttpClientErrorException e) {
			throw new SupabaseAuthException(e.getStatusCode().value(), e.getResponseBodyAsString());
		} catch (RestClientResponseException e) {
			throw new SupabaseAuthException(e.getStatusCode().value(), e.getResponseBodyAsString());
		}
	}

	private String buildUsersFilterUrl(String email, String userId) {
		String base = supabaseUrl.replaceAll("/+$", "") + "/rest/v1/users";
		if (userId != null && !userId.isBlank()) {
			String encodedUserID = URLEncoder.encode(userId.trim(), StandardCharsets.UTF_8);
			return base + "?id=eq." + encodedUserID;
		}
		String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
		return base + "?email=eq." + encodedEmail;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> createAuthUser(String email, String password) {
		if (!isServiceRoleConfigured()) {
			throw new IllegalStateException("supabase_service_role_not_configured");
		}

		String url = supabaseUrl.replaceAll("/+$", "") + "/auth/v1/admin/users";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("apikey", serviceRoleKey);
		headers.set("Authorization", "Bearer " + serviceRoleKey);

		Map<String, Object> payload = Map.of(
				"email", email,
				"password", password,
				"email_confirm", true);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

		try {
			ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
			Map<String, Object> body = response.getBody();
			if (body == null) {
				return Map.of("email", email, "message", "created");
			}
			return body;
		} catch (HttpClientErrorException e) {
			throw new SupabaseAuthException(e.getStatusCode().value(), e.getResponseBodyAsString());
		} catch (RestClientResponseException e) {
			throw new SupabaseAuthException(e.getStatusCode().value(), e.getResponseBodyAsString());
		}
	}

	public static final class SupabaseAuthException extends RuntimeException {
		private final int status;
		private final String responseBody;

		public SupabaseAuthException(int status, String responseBody) {
			super(responseBody);
			this.status = status;
			this.responseBody = responseBody;
		}

		public int getStatus() {
			return status;
		}

		public String getResponseBody() {
			return responseBody;
		}
	}

	public static final class UserProfileNotFoundException extends RuntimeException {
		public UserProfileNotFoundException(String message) {
			super(message);
		}
	}
}

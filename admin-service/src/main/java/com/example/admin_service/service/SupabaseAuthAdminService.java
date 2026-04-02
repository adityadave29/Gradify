package com.example.admin_service.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
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
}

package com.example.user_service.services;
import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.UserProfileUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon.key}")
    private String supabaseAnonKey;

    private final RestTemplate restTemplate;  // ← injected

    public AuthService(RestTemplate restTemplate) {  // ← constructor injection
        this.restTemplate = restTemplate;
    }
    public Map<String, Object> signup(LoginRequest request) {
        String url = supabaseUrl + "/auth/v1/signup";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseAnonKey);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(
                Map.of("email", request.getEmail(), "password", request.getPassword()),
                headers
        );

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to create user: " + e.getResponseBodyAsString());
        }
    }

    public Map<String, Object> updateUserProfile(UserProfileUpdateRequest request) {
        String url = supabaseUrl + "/rest/v1/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseAnonKey);
        headers.set("Authorization", "Bearer " + supabaseAnonKey);
        headers.set("Prefer", "return=representation");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(
                Map.of("id", UUID.randomUUID().toString(), "email", request.getEmail(), "name", request.getName(), "role", request.getRole()),
                headers
        );

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return Map.of("success", true, "message", "User profile updated successfully", "data", response.getBody());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to update user profile: " + e.getResponseBodyAsString());
        }
    }

    public Map<String, Object> login(LoginRequest request) {
        String url = supabaseUrl + "/auth/v1/token?grant_type=password";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseAnonKey);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(
                Map.of("email", request.getEmail(), "password", request.getPassword()),
                headers
        );

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Invalid email or password.");
        }
    }
}
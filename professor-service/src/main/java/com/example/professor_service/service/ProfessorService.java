package com.example.professor_service.service;

import com.example.professor_service.model.Course;
import com.example.professor_service.model.StudentDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProfessorService {

    private final RestTemplate restTemplate;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon.key}")
    private String supabaseAnonKey;

    @Value("${supabase.service_role.key:}")
    private String supabaseServiceRoleKey;

    public ProfessorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Course> getCoursesByProfessor(String professorId) {
        String url = supabaseUrl + "/rest/v1/courses?professor_id=eq." + professorId + "&select=*";

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseAnonKey);
        headers.set("Authorization", "Bearer " + supabaseAnonKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Course[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Course[].class
        );

        if (response.getBody() != null) {
            return Arrays.asList(response.getBody());
        }
        return List.of();
    }

    public List<StudentDTO> getEnrolledStudents(Integer courseId) {
        String url = supabaseUrl + "/rest/v1/enrollments?course_id=eq." + courseId + "&select=student:users(name,email)";

        // Use service_role key if provided to bypass RLS, fallback to anon key
        String key = (supabaseServiceRoleKey != null && !supabaseServiceRoleKey.isBlank()) 
                     ? supabaseServiceRoleKey 
                     : supabaseAnonKey;

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", key);
        headers.set("Authorization", "Bearer " + key);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map[].class
        );

        if (response.getBody() != null) {
            return Arrays.stream(response.getBody())
                    .map(item -> {
                        Map<String, String> studentMap = (Map<String, String>) item.get("student");
                        return new StudentDTO(studentMap.get("name"), studentMap.get("email"));
                    })
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}

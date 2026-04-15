package com.example.student_service.service;

import com.example.student_service.model.EnrolledCourse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final RestTemplate restTemplate;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon.key}")
    private String supabaseAnonKey;

    @Value("${supabase.service_role.key:}")
    private String supabaseServiceRoleKey;

    public StudentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Step 0 : Resolve the *public users table* ID from the student's email.
     *          The Supabase Auth UUID stored in the browser differs from the
     *          UUID that the admin service inserted into public.users.
     * Step 1 : Fetch the course_id list from enrollments for that public UUID.
     * Step 2 : Fetch full course details for those IDs.
     */
    public List<EnrolledCourse> getEnrolledCourses(String email) {
        String key = getEffectiveKey();
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", key);
        headers.set("Authorization", "Bearer " + key);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // ── Step 0: Resolve public user ID from email ─────────────────────────
        String studentId;
        try {
            String userUrl = supabaseUrl
                    + "/rest/v1/users?email=eq." + email
                    + "&select=id";

            ResponseEntity<Map[]> userResp = restTemplate.exchange(
                    userUrl, HttpMethod.GET, entity, Map[].class
            );

            if (userResp.getBody() == null || userResp.getBody().length == 0) {
                System.err.println("Student not found in public.users for email: " + email);
                return List.of();
            }
            studentId = (String) userResp.getBody()[0].get("id");
            System.out.println("Resolved student public ID: " + studentId + " for email: " + email);

        } catch (HttpStatusCodeException e) {
            System.err.println("Supabase Error (GET user by email): " + e.getResponseBodyAsString());
            return List.of();
        }

        // ── Step 1: Get course_ids from enrollments for this student ──────────
        List<Integer> courseIds;
        try {
            String enrollUrl = supabaseUrl
                    + "/rest/v1/enrollments?student_id=eq." + studentId
                    + "&select=course_id";

            ResponseEntity<Map[]> enrollResp = restTemplate.exchange(
                    enrollUrl, HttpMethod.GET, entity, Map[].class
            );

            if (enrollResp.getBody() == null || enrollResp.getBody().length == 0) {
                System.out.println("No enrollments found for studentId: " + studentId);
                return List.of();
            }

            courseIds = Arrays.stream(enrollResp.getBody())
                    .map(row -> ((Number) row.get("course_id")).intValue())
                    .collect(Collectors.toList());

            System.out.println("Found course_ids: " + courseIds);

        } catch (HttpStatusCodeException e) {
            System.err.println("Supabase Error (GET enrollments): " + e.getResponseBodyAsString());
            return List.of();
        }

        // ── Step 2: Fetch full course details for those IDs ───────────────────
        String idList = courseIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        try {
            String coursesUrl = supabaseUrl
                    + "/rest/v1/courses?id=in.(" + idList + ")&select=*";

            ResponseEntity<EnrolledCourse[]> coursesResp = restTemplate.exchange(
                    coursesUrl, HttpMethod.GET, entity, EnrolledCourse[].class
            );

            if (coursesResp.getBody() != null) {
                System.out.println("Returning " + coursesResp.getBody().length + " courses.");
                return Arrays.asList(coursesResp.getBody());
            }
        } catch (HttpStatusCodeException e) {
            System.err.println("Supabase Error (GET courses): " + e.getResponseBodyAsString());
        }

        return List.of();
    }

    private String getEffectiveKey() {
        return (supabaseServiceRoleKey != null && !supabaseServiceRoleKey.isBlank())
                ? supabaseServiceRoleKey
                : supabaseAnonKey;
    }
}

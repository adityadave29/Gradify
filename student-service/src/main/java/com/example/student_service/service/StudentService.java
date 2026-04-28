package com.example.student_service.service;

import com.example.student_service.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Map<String, Double> GRADE_POINTS = new HashMap<>() {{
        put("A", 4.0);
        put("A-", 3.7);
        put("B+", 3.4);
        put("B", 3.0);
        put("B-", 2.7);
        put("C+", 2.3);
        put("C", 2.0);
    }};

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

    public List<EnrolledCourse> getEnrolledCourses(String email) {
        String key = getEffectiveKey();
        HttpEntity<Void> entity = buildEntity(key);
        String studentId = resolveStudentIdByEmail(email, entity);
        if (studentId == null) return List.of();
        List<Integer> courseIds = fetchCourseIds(studentId, entity);
        if (courseIds.isEmpty()) return List.of();
        String idList = courseIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        try {
            String url = supabaseUrl + "/rest/v1/courses?id=in.(" + idList + ")&select=*";
            ResponseEntity<EnrolledCourse[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, EnrolledCourse[].class);
            if (resp.getBody() != null) return Arrays.asList(resp.getBody());
        } catch (HttpStatusCodeException e) {
            System.err.println("Supabase Error (GET courses): " + e.getResponseBodyAsString());
        }
        return List.of();
    }

    public List<StudentRanking> getCourseRankings(Integer courseId, String currentEmail) {
        String key = getEffectiveKey();
        HttpEntity<Void> entity = buildEntity(key);
        String currentStudentId = resolveStudentIdByEmail(currentEmail, entity);

        List<EvaluationComponent> components = new ArrayList<>();
        try {
            String url = supabaseUrl + "/rest/v1/evaluation_components?course_id=eq." + courseId + "&select=*&order=created_at.asc";
            ResponseEntity<EvaluationComponent[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, EvaluationComponent[].class);
            if (resp.getBody() != null) components = Arrays.asList(resp.getBody());
        } catch (HttpStatusCodeException e) {
            System.err.println("Supabase Error (GET components): " + e.getResponseBodyAsString());
        }

        List<Mark> marks = new ArrayList<>();
        try {
            String url = supabaseUrl + "/rest/v1/marks?course_id=eq." + courseId + "&select=*";
            ResponseEntity<Mark[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, Mark[].class);
            if (resp.getBody() != null) marks = Arrays.asList(resp.getBody());
        } catch (HttpStatusCodeException e) {
            System.err.println("Supabase Error (GET marks): " + e.getResponseBodyAsString());
        }

        Map<String, String[]> studentInfo = new LinkedHashMap<>();
        try {
            String url = supabaseUrl + "/rest/v1/enrollments?course_id=eq." + courseId + "&select=student:users(id,name,email)";
            ResponseEntity<Map[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, Map[].class);
            if (resp.getBody() != null) {
                for (Map<?, ?> row : resp.getBody()) {
                    Map<String, Object> stu = (Map<String, Object>) row.get("student");
                    if (stu != null) {
                        studentInfo.put((String) stu.get("id"), new String[]{(String) stu.get("name"), (String) stu.get("email")});
                    }
                }
            }
        } catch (HttpStatusCodeException e) {
            System.err.println("Supabase Error (GET enrolled students): " + e.getResponseBodyAsString());
        }

        if (studentInfo.isEmpty()) return List.of();

        Map<String, Map<Integer, Integer>> marksByStudent = new HashMap<>();
        for (Mark m : marks) {
            marksByStudent.computeIfAbsent(m.getStudentId(), k -> new HashMap<>()).put(m.getComponentId(), m.getMarksObtained());
        }

        int totalWeightage = components.stream().mapToInt(c -> c.getWeightage() != null ? c.getWeightage() : 0).sum();

        List<StudentRanking> rankings = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : studentInfo.entrySet()) {
            String sid = entry.getKey();
            String[] info = entry.getValue();
            Map<Integer, Integer> sm = marksByStudent.getOrDefault(sid, new HashMap<>());
            List<ComponentMark> compMarks = new ArrayList<>();
            double totalWeightedScore = 0.0;

            for (EvaluationComponent comp : components) {
                Integer obtainedRaw = sm.get(comp.getId());
                double weightedContribution = 0.0;
                
                // Fallback to 100 if maxMarks is missing in DB
                int max = (comp.getMaxMarks() != null && comp.getMaxMarks() > 0) ? comp.getMaxMarks() : 100;
                
                if (obtainedRaw != null && comp.getWeightage() != null) {
                    weightedContribution = (obtainedRaw.doubleValue() / (double)max) * comp.getWeightage().doubleValue();
                }
                
                totalWeightedScore += weightedContribution;
                compMarks.add(new ComponentMark(comp.getId(), comp.getComponentName(), comp.getWeightage(), comp.getMaxMarks(), obtainedRaw, weightedContribution));
            }

            rankings.add(new StudentRanking(null, sid, info[0] != null ? info[0] : "Unknown", info[1], compMarks, totalWeightedScore, totalWeightage, null, sid.equals(currentStudentId)));
        }

        rankings.sort((a, b) -> Double.compare(b.getTotalWeightedScore(), a.getTotalWeightedScore()));
        int rank = 1;
        for (int i = 0; i < rankings.size(); i++) {
            if (i > 0 && !rankings.get(i).getTotalWeightedScore().equals(rankings.get(i - 1).getTotalWeightedScore())) {
                rank = i + 1;
            }
            rankings.get(i).setRank(rank);
        }

        assignGradesToRankings(rankings, courseId);

        return rankings;
    }

    private void assignGradesToRankings(List<StudentRanking> rankings, Integer courseId) {
        List<GradeDistribution> distributions = getGradeDistribution(courseId);
        if (distributions.isEmpty() || rankings.isEmpty()) return;

        // Sort distributions: A, A-, B+, B, B-, C+, C
        List<String> gradeOrder = Arrays.asList("A", "A-", "B+", "B", "B-", "C+", "C");
        distributions.sort(Comparator.comparingInt(d -> {
            int idx = gradeOrder.indexOf(d.getGrade());
            return idx == -1 ? 99 : idx;
        }));

        int totalStudents = rankings.size();
        int currentRankIndex = 0;

        for (GradeDistribution dist : distributions) {
            int countForThisGrade = (int) Math.round((dist.getPercentage() / 100.0) * totalStudents);
            for (int i = 0; i < countForThisGrade && currentRankIndex < totalStudents; i++) {
                rankings.get(currentRankIndex).setGrade(dist.getGrade());
                currentRankIndex++;
            }
        }

        // Assign default grade (C or D/Pass) to remaining students if distribution didn't cover all
        while (currentRankIndex < totalStudents) {
            rankings.get(currentRankIndex).setGrade("C");
            currentRankIndex++;
        }
    }

    public Double getStudentCGPA(String email) {
        List<EnrolledCourse> courses = getEnrolledCourses(email);
        if (courses.isEmpty()) return 0.0;

        double totalPoints = 0.0;
        int gradedCourses = 0;

        for (EnrolledCourse course : courses) {
            List<StudentRanking> rankings = getCourseRankings(course.getId(), email);
            Optional<StudentRanking> currentStudent = rankings.stream()
                    .filter(StudentRanking::getIsCurrentUser)
                    .findFirst();

            if (currentStudent.isPresent() && currentStudent.get().getGrade() != null) {
                Double points = GRADE_POINTS.getOrDefault(currentStudent.get().getGrade(), 1.5); // Fallback to 1.5 for anything else
                totalPoints += points;
                gradedCourses++;
            }
        }

        return gradedCourses > 0 ? totalPoints / gradedCourses : 0.0;
    }

    public List<GradeDistribution> getGradeDistribution(Integer courseId) {
        String key = getEffectiveKey();
        HttpEntity<Void> entity = buildEntity(key);
        try {
            String url = supabaseUrl + "/rest/v1/grade_distribution?course_id=eq." + courseId + "&select=*";
            ResponseEntity<GradeDistribution[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, GradeDistribution[].class);
            if (resp.getBody() != null) return Arrays.asList(resp.getBody());
        } catch (HttpStatusCodeException e) {
            System.err.println("Supabase Error (GET grade distribution): " + e.getResponseBodyAsString());
        }
        return List.of();
    }

    private String resolveStudentIdByEmail(String email, HttpEntity<Void> entity) {
        try {
            String url = supabaseUrl + "/rest/v1/users?email=eq." + email + "&select=id";
            ResponseEntity<Map[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, Map[].class);
            if (resp.getBody() != null && resp.getBody().length > 0) return (String) resp.getBody()[0].get("id");
        } catch (HttpStatusCodeException e) {
            System.err.println("Supabase Error (GET user by email): " + e.getResponseBodyAsString());
        }
        return null;
    }

    private List<Integer> fetchCourseIds(String studentId, HttpEntity<Void> entity) {
        try {
            String url = supabaseUrl + "/rest/v1/enrollments?student_id=eq." + studentId + "&select=course_id";
            ResponseEntity<Map[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, Map[].class);
            if (resp.getBody() != null && resp.getBody().length > 0) {
                return Arrays.stream(resp.getBody()).map(r -> ((Number) r.get("course_id")).intValue()).collect(Collectors.toList());
            }
        } catch (HttpStatusCodeException e) {
            System.err.println("Supabase Error (GET enrollments): " + e.getResponseBodyAsString());
        }
        return List.of();
    }

    private HttpEntity<Void> buildEntity(String key) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", key);
        headers.set("Authorization", "Bearer " + key);
        return new HttpEntity<>(headers);
    }

    private String getEffectiveKey() {
        return (supabaseServiceRoleKey != null && !supabaseServiceRoleKey.isBlank()) ? supabaseServiceRoleKey : supabaseAnonKey;
    }
}

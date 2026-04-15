package com.example.student_service.controller;

import com.example.student_service.model.EnrolledCourse;
import com.example.student_service.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * GET /api/student/courses?email=student@example.com
     *
     * Uses email (always available in auth session) to look up the correct
     * public-users-table UUID, avoiding the Auth-UUID vs public-UUID mismatch.
     */
    @GetMapping("/courses")
    public List<EnrolledCourse> getEnrolledCourses(@RequestParam String email) {
        return studentService.getEnrolledCourses(email);
    }
}

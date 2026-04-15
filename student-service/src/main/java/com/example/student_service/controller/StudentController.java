package com.example.student_service.controller;

import com.example.student_service.model.EnrolledCourse;
import com.example.student_service.model.StudentRanking;
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

    /** GET /api/student/courses?email=student@example.com */
    @GetMapping("/courses")
    public List<EnrolledCourse> getEnrolledCourses(@RequestParam String email) {
        return studentService.getEnrolledCourses(email);
    }

    /**
     * GET /api/student/courses/{courseId}/rankings?email=student@example.com
     * Returns all students ranked by total marks; flags the requesting student.
     */
    @GetMapping("/courses/{courseId}/rankings")
    public List<StudentRanking> getCourseRankings(
            @PathVariable Integer courseId,
            @RequestParam String email) {
        return studentService.getCourseRankings(courseId, email);
    }
}

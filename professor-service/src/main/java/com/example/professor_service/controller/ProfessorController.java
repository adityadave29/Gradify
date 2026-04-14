package com.example.professor_service.controller;

import com.example.professor_service.model.Course;
import com.example.professor_service.model.StudentDTO;
import com.example.professor_service.service.ProfessorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professor")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping("/courses")
    public List<Course> getCourses(@RequestParam String professorId) {
        return professorService.getCoursesByProfessor(professorId);
    }

    @GetMapping("/courses/{courseId}/students")
    public List<StudentDTO> getStudents(@PathVariable Integer courseId) {
        return professorService.getEnrolledStudents(courseId);
    }

    @GetMapping("/courses/{courseId}")
    public Course getCourse(@PathVariable Integer courseId) {
        return professorService.getCourseByCourseId(courseId);
    }
}

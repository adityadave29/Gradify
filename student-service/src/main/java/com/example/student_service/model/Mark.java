package com.example.student_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mark {
    private Integer id;

    @JsonProperty("student_id")
    private String studentId;

    @JsonProperty("course_id")
    private Integer courseId;

    @JsonProperty("component_id")
    private Integer componentId;

    @JsonProperty("marks_obtained")
    private Integer marksObtained;

    @JsonProperty("created_at")
    private String createdAt;
}

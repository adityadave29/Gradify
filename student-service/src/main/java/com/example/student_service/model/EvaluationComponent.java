package com.example.student_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationComponent {
    private Integer id;

    @JsonProperty("course_id")
    private Integer courseId;

    @JsonProperty("component_name")
    private String componentName;

    private Integer weightage;

    @JsonProperty("max_marks")
    private Integer maxMarks;

    @JsonProperty("created_at")
    private String createdAt;
}

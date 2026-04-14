package com.example.professor_service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvaluationComponent {
    private Integer id;

    @JsonProperty("course_id")
    private Integer courseId;

    @JsonProperty("component_name")
    private String componentName;

    private Integer weightage;

    @JsonProperty("created_at")
    private String createdAt;
}

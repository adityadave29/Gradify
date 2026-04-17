package com.example.student_service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GradeDistribution {
    private Integer id;

    @JsonProperty("course_id")
    private Integer courseId;

    private String grade;
    private Integer percentage;

    @JsonProperty("created_at")
    private String createdAt;
}

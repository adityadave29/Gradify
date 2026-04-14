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
public class Mark {
    private Integer id;

    @JsonProperty("student_id")
    private String studentId;

    @JsonProperty("course_id")
    private Integer courseId;

    @JsonProperty("component_id")
    private Integer componentId;

    @JsonProperty("marks_obtained")
    private Double marksObtained;

    @JsonProperty("created_at")
    private String createdAt;
}

package com.example.student_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComponentMark {
    @JsonProperty("component_id")
    private Integer componentId;

    @JsonProperty("component_name")
    private String componentName;

    private Integer weightage;

    @JsonProperty("max_marks")
    private Integer maxMarks;

    @JsonProperty("obtained_marks")
    private Integer obtainedMarks; // null if not yet graded

    @JsonProperty("weighted_score")
    private Double weightedScore; 
}

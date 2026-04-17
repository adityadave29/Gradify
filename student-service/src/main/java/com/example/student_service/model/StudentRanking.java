package com.example.student_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRanking {
    private Integer rank;

    @JsonProperty("student_id")
    private String studentId;

    @JsonProperty("student_name")
    private String studentName;

    @JsonProperty("student_email")
    private String studentEmail;

    private List<ComponentMark> components;

    @JsonProperty("total_weighted_score")
    private Double totalWeightedScore;

    @JsonProperty("total_weightage")
    private Integer totalWeightage;

    private String grade;

    @JsonProperty("is_current_user")
    private Boolean isCurrentUser;
}

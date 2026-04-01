package com.example.user_service.dto;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String email;
    private String name;
    private String role;
}

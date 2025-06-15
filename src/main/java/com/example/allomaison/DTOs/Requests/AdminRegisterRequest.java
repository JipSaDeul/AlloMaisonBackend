package com.example.allomaison.DTOs.Requests;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String adminName;
    private String rawPassword;
}

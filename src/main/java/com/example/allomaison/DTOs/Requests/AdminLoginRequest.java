package com.example.allomaison.DTOs.Requests;

import lombok.Data;

@Data
public class AdminLoginRequest {
    private String adminName;
    private String password;
}

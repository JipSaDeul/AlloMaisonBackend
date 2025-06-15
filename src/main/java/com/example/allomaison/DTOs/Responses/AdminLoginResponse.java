package com.example.allomaison.DTOs.Responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLoginResponse {
    private String token;
    private Long adminId;
    private String adminName;
}

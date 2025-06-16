package com.example.allomaison.DTOs.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Long userId;
    private String firstName;
    private String lastName;
    private String userName;
    private Gender gender;
    private String birthday;   // ISO string, e.g. "1990-01-01"
    private String email;
    private String avatarUrl;
    private String createdAt;  // ISO string, e.g. "2024-06-15T12:34:56Z"
    private Role role;

    public enum Gender {
        male, female
    }

    public enum Role {
        customer, provider
    }
}

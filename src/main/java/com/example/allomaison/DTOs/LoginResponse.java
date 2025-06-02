// LoginResponse.java
package com.example.allomaison.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String role;       // "customer" / "provider"
    private Long userId;
    private String avatarUrl;
    private String userName;
}

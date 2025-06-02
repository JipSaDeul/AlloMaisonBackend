package com.example.allomaison.DTOs;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRegisterRequest {
    private String firstName;
    private String lastName;
    private String userName;
    private String gender; // 'male' | 'female' | null
    private String birthday; // yyyy-MM-dd or ISO
    private String email;
    private MultipartFile avatar; // uploaded file
    private String password;
}

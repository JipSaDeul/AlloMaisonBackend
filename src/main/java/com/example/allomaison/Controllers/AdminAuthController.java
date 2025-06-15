package com.example.allomaison.Controllers;

import com.example.allomaison.DTOs.Requests.AdminLoginRequest;
import com.example.allomaison.DTOs.Responses.AdminLoginResponse;
import com.example.allomaison.DTOs.Responses.ErrorResponse;
import com.example.allomaison.Security.AdminJwtService;
import com.example.allomaison.Services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminService adminService;
    private final AdminJwtService adminJwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest request) {
        return adminService.login(request.getAdminName(), request.getPassword())
                .map(admin -> {
                    String token = adminJwtService.generateToken(admin);
                    return AdminLoginResponse.builder()
                            .token(token)
                            .adminId(admin.getAdminId())
                            .adminName(admin.getAdminName())
                            .build();
                })
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body(
                        ErrorResponse.builder()
                                .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                                .message("Invalid admin name or password")
                                .build()
                ));
    }
}

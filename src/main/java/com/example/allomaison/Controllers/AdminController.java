package com.example.allomaison.Controllers;

import com.example.allomaison.DTOs.AdminDTO;
import com.example.allomaison.DTOs.ProviderApplicationDTO;
import com.example.allomaison.DTOs.Requests.AdminRegisterRequest;
import com.example.allomaison.DTOs.Responses.ErrorResponse;
import com.example.allomaison.DTOs.Responses.SuccessResponse;
import com.example.allomaison.Entities.ProviderApplication;
import com.example.allomaison.Security.AdminJwtService;
import com.example.allomaison.Services.AdminService;
import com.example.allomaison.Services.NoticeService;
import com.example.allomaison.DTOs.Requests.NoticeRequest;
import com.example.allomaison.Services.ProviderApplicationService;
import com.example.allomaison.Services.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminJwtService adminJwtService;
    private final NoticeService noticeService;
    private final ProviderService providerService;
    private final ProviderApplicationService providerApplicationService;
    private final AdminService adminService;

    private Optional<AdminDTO> extractAdminFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String token = authHeader.substring(7);
        return adminJwtService.parseToken(token);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentAdmin(@RequestHeader("Authorization") String authHeader) {
        Optional<AdminDTO> adminOpt = extractAdminFromToken(authHeader);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(401).body(ErrorResponse.builder()
                    .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                    .message("Missing or invalid admin token")
                    .build());
        }

        return ResponseEntity.ok(adminOpt.get());
    }

    @PostMapping("/notices")
    public ResponseEntity<?> postSystemNotice(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody NoticeRequest request
    ) {
        Optional<AdminDTO> adminOpt = extractAdminFromToken(authHeader);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(401).body(ErrorResponse.builder()
                    .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                    .message("Missing or invalid admin token")
                    .build());
        }

        if (request.getTargets() == null || request.getType() == null || request.getTitle() == null || request.getContent() == null) {
            return ResponseEntity.badRequest().body(ErrorResponse.builder()
                    .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                    .message("Missing required notice fields")
                    .build());
        }

        return noticeService.postNotice(request)
                .<ResponseEntity<?>>map(notice -> ResponseEntity.ok(new SuccessResponse()))
                .orElseGet(() -> ResponseEntity.status(500).body(
                        ErrorResponse.builder()
                                .errorCode(ErrorResponse.ErrorCode.SERVER_ERROR)
                                .message("Failed to post notice")
                                .build()));
    }

    @GetMapping("/notices/global")
    public ResponseEntity<?> getAllGlobalNotices(@RequestHeader("Authorization") String authHeader) {
        Optional<AdminDTO> adminOpt = extractAdminFromToken(authHeader);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid admin token")
                            .build());
        }

        return ResponseEntity.ok(noticeService.getAllGlobalNotices());
    }

    @GetMapping("/notices/sys")
    public ResponseEntity<?> getAllSystemNotices(@RequestHeader("Authorization") String authHeader) {
        Optional<AdminDTO> adminOpt = extractAdminFromToken(authHeader);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid admin token")
                            .build());
        }

        return ResponseEntity.ok(noticeService.getAllSystemNotices());
    }


    @GetMapping("/notices/providers")
    public ResponseEntity<?> getAllProviderNotices(@RequestHeader("Authorization") String authHeader) {
        Optional<AdminDTO> adminOpt = extractAdminFromToken(authHeader);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid admin token")
                            .build());
        }

        return ResponseEntity.ok(noticeService.getAllProviderNotices());
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getApplications(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "status", required = false) String statusStr
    ) {
        Optional<AdminDTO> adminOpt = extractAdminFromToken(authHeader);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid admin token")
                            .build());
        }

        ProviderApplication.ApplicationStatus status = ProviderApplication.ApplicationStatus.PENDING; // default
        if (statusStr != null) {
            try {
                status = ProviderApplication.ApplicationStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                        ErrorResponse.builder()
                                .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                                .message("Invalid application status: " + statusStr)
                                .build());
            }
        }

        List<ProviderApplicationDTO> result = providerApplicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/applications/{applicationId}/review")
    public ResponseEntity<?> reviewApplication(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long applicationId,
            @RequestParam("action") String action
    ) {
        Optional<AdminDTO> adminOpt = extractAdminFromToken(authHeader);
        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid admin token")
                            .build());
        }

        Optional<ProviderApplicationDTO> applicationOpt = providerApplicationService.getApplicationById(applicationId);
        if (applicationOpt.isEmpty()) {
            return ResponseEntity.status(404).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_NOT_FOUND)
                            .message("Application not found")
                            .build());
        }

        boolean success;
        switch (action.toLowerCase()) {
            case "approve" -> success = providerService.approveApplicationAndCreateProvider(applicationOpt.get());
            case "reject" -> success = providerService.rejectApplication(applicationOpt.get());
            default -> {
                return ResponseEntity.badRequest().body(
                        ErrorResponse.builder()
                                .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                                .message("Invalid action: must be 'approve' or 'reject'")
                                .build());
            }
        }

        if (!success) {
            return ResponseEntity.status(409).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_DUPLICATE)
                            .message("Application already reviewed or provider exists")
                            .build());
        }

        return ResponseEntity.ok(new SuccessResponse());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AdminRegisterRequest request
    ) {
        Optional<AdminDTO> creatorOpt = extractAdminFromToken(authHeader);
        if (creatorOpt.isEmpty()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                            .message("Missing or invalid admin token")
                            .build());
        }

        String name = request.getAdminName();
        String password = request.getRawPassword();

        if (name == null || name.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Admin name and password must not be blank")
                            .build());
        }

        Optional<AdminDTO> created = adminService.register(name, password, creatorOpt.get());

        return created.map(admin -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("adminId", admin.getAdminId());
                    response.put("adminName", admin.getAdminName());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> error = new HashMap<>();
                    error.put("errorCode", ErrorResponse.ErrorCode.AUTH_FORBIDDEN.toString());
                    error.put("message", "Failed to register admin (possibly unauthorized or duplicate name)");
                    return ResponseEntity.status(403).body(error);
                });
    }
}

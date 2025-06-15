package com.example.allomaison.Controllers;

import com.example.allomaison.DTOs.RegisterResult;
import com.example.allomaison.DTOs.Requests.LoginRequest;
import com.example.allomaison.DTOs.Requests.UserRegisterJsonRequest;
import com.example.allomaison.DTOs.Requests.UserRegisterRequest;
import com.example.allomaison.DTOs.Responses.LoginResponse;
import com.example.allomaison.DTOs.Responses.SuccessResponse;
import com.example.allomaison.Security.JwtService;
import com.example.allomaison.Services.ProviderService;
import com.example.allomaison.Services.UserService;
import com.example.allomaison.Utils.EmailHelper;
import com.example.allomaison.DTOs.Responses.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ProviderService providerService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userService.login(request.getEmail(), request.getPassword())
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    String role = providerService.isProvider(user.getUserId()) ? "provider" : "customer";
                    return LoginResponse.builder()
                            .token(token)
                            .role(role)
                            .userId(user.getUserId())
                            .avatarUrl(user.getAvatarUrl())
                            .userName(user.getUserName())
                            .build();
                })
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).body(
                        ErrorResponse.builder()
                                .errorCode(ErrorResponse.ErrorCode.AUTH_INVALID_CREDENTIALS)
                                .message("Invalid email or password")
                                .build()
                ));
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@ModelAttribute UserRegisterRequest request) {
        return handleRegistration(request);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerJson(@RequestBody UserRegisterJsonRequest jsonRequest) {
        UserRegisterRequest converted = new UserRegisterRequest();
        BeanUtils.copyProperties(jsonRequest, converted);
        converted.setAvatar(null);
        return handleRegistration(converted);
    }

    private ResponseEntity<?> handleRegistration(UserRegisterRequest request) {
        if (!EmailHelper.isPossiblyValidEmail(request.getEmail())) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Invalid email format")
                            .build());
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_MISSING_FIELD)
                            .message("Password must not be empty")
                            .build());
        }

        if (!request.isAgree()) {
            return ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_MISSING_FIELD)
                            .message("You must agree to the terms")
                            .build());
        }

        RegisterResult result = userService.register(request);
        if (result.isSuccess()) {
            return ResponseEntity.ok(new SuccessResponse());
        }

        return switch (result.getReason()) {
            case DUPLICATE_EMAIL_OR_USERNAME -> ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.AUTH_ALREADY_REGISTERED)
                            .message("Email or username already exists")
                            .build());

            case AVATAR_TOO_LARGE -> ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_TYPE)
                            .message("Avatar file too large (max 5MB)")
                            .build());

            case AVATAR_UPLOAD_FAILED -> ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INPUT_INVALID_FILE_FORMAT)
                            .message("Avatar upload failed")
                            .build());

            case UNKNOWN -> ResponseEntity.status(401).body(
                    ErrorResponse.builder()
                            .errorCode(ErrorResponse.ErrorCode.INTERNAL_ERROR)
                            .message("Registration failed")
                            .build());
        };
    }

}

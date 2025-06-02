package com.example.allomaison.Controllers;

import com.example.allomaison.DTOs.*;
import com.example.allomaison.Security.JwtService;
import com.example.allomaison.Services.ProviderService;
import com.example.allomaison.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ProviderService providerService;
    private final JwtService jwtService;


    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var res = userService.login(request.getEmail(), request.getPassword())
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    String role = providerService.isProvider(user.getUserId())? "provider" : "customer";
                    LoginResponse response = LoginResponse.builder()
                            .token(token)
                            .role(role)
                            .userId(user.getUserId())
                            .avatarUrl(user.getAvatarUrl())
                            .userName(user.getUserName())
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(401).body(LoginResponse.builder().token(null).build()));
        System.out.println(res);
        return res;
    }
}

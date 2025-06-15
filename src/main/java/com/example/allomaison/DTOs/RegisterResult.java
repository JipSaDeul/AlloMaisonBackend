package com.example.allomaison.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegisterResult {
    private boolean success;
    private UserDTO user;
    private ErrorReason reason;

    public enum ErrorReason {
        DUPLICATE_EMAIL_OR_USERNAME,
        AVATAR_TOO_LARGE,
        AVATAR_UPLOAD_FAILED,
        UNKNOWN
    }

    public static RegisterResult failure(ErrorReason reason) {
        return RegisterResult.builder().success(false).reason(reason).build();
    }

    public static RegisterResult success(UserDTO user) {
        return RegisterResult.builder().success(true).user(user).build();
    }
}

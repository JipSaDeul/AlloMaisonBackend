package com.example.allomaison.DTOs.Responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private ErrorCode errorCode;
    private String message;

    public enum ErrorCode {
        INPUT_INVALID_TYPE,
        INPUT_INVALID_FILE_FORMAT,
        INPUT_FILE_TOO_LARGE,
        INPUT_MISSING_FIELD,
        INPUT_INVALID_ZERO_VALUE,
        AUTH_INVALID_CREDENTIALS,
        AUTH_ALREADY_REGISTERED,
        AUTH_UNAUTHORIZED,
        AUTH_FORBIDDEN, INPUT_NOT_FOUND, SERVER_ERROR, INPUT_DUPLICATE, INTERNAL_ERROR
    }
}

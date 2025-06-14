package com.example.allomaison.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", "ok", data);
    }

    @SuppressWarnings("unused")
    public static <T> ApiResponse<T> fail(T data) {
        return new ApiResponse<>("fail", "", data);
    }

    public static <T> ApiResponse<T> fail(String message, T data) {
        return new ApiResponse<>("fail", message, data);
    }
}

package com.example.allomaison.DTOs.Requests;

public record MessageRequest(
        Long senderId,
        Long receiverId,
        String content
) {}

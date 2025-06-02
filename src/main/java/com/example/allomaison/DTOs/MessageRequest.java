package com.example.allomaison.DTOs;

public record MessageRequest(
        Long senderId,
        Long receiverId,
        String content
) {}

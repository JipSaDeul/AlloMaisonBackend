package com.example.allomaison.DTOs;

import java.sql.Timestamp;

public record ChatMessageDTO(
        Long messageId,
        Long chatId,
        Long senderId,
        Long receiverId,
        String content,
        Timestamp sentTime
) {}

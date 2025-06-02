package com.example.allomaison.DTOs;

import java.sql.Timestamp;

public record ConversationDTO(
        Long chatId,
        Long user1Id,
        Long user2Id,
        Long lastSenderId,
        Timestamp lastSentTime
) {}

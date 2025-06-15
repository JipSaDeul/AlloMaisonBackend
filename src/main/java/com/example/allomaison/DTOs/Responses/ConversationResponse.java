package com.example.allomaison.DTOs.Responses;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class ConversationResponse {
    private Long chatId;
    private String contactName;
    private String lastMessage;
    private Timestamp updatedAt;
}

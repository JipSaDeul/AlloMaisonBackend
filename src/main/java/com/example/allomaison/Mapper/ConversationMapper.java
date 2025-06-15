package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.ConversationDTO;
import com.example.allomaison.DTOs.Responses.ConversationResponse;
import com.example.allomaison.Entities.ChatMessage;
import com.example.allomaison.Entities.Conversation;

import java.sql.Timestamp;

public class ConversationMapper {

    public static ConversationDTO toDTO(Conversation entity, Long lastSenderId, Timestamp lastSentTime) {
        return new ConversationDTO(
                entity.getChatId(),
                entity.getUser1Id(),
                entity.getUser2Id(),
                lastSenderId,
                lastSentTime
        );
    }

    public static ConversationResponse toResponse(
            Conversation convo,
            ChatMessage lastMessage,
            String contactName
    ) {
        return ConversationResponse.builder()
                .chatId(convo.getChatId())
                .contactName(contactName)
                .lastMessage(lastMessage != null ? lastMessage.getContent() : "")
                .updatedAt(lastMessage != null ? lastMessage.getSentTime() : null)
                .build();
    }
}

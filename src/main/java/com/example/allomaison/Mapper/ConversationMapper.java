package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.ConversationDTO;
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
}

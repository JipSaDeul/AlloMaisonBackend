package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.ChatMessageDTO;
import com.example.allomaison.Entities.ChatMessage;
import com.example.allomaison.Entities.Conversation;

public class ChatMessageMapper {

    public static ChatMessageDTO toDTO(ChatMessage message) {
        Conversation convo = message.getConversation();
        Long senderId = message.isSentByUser1() ? convo.getUser1Id() : convo.getUser2Id();
        Long receiverId = message.isSentByUser1() ? convo.getUser2Id() : convo.getUser1Id();

        return new ChatMessageDTO(
                message.getMessageId(),
                convo.getChatId(),
                senderId,
                receiverId,
                message.getContent(),
                message.getSentTime()
        );
    }
}

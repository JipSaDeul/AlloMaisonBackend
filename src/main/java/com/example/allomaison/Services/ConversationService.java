package com.example.allomaison.Services;

import com.example.allomaison.DTOs.ChatMessageDTO;
import com.example.allomaison.DTOs.ConversationDTO;
import com.example.allomaison.DTOs.MessageRequest;
import com.example.allomaison.Entities.ChatMessage;
import com.example.allomaison.Entities.Conversation;
import com.example.allomaison.Mapper.ChatMessageMapper;
import com.example.allomaison.Mapper.ConversationMapper;
import com.example.allomaison.Repositories.ChatMessageRepository;
import com.example.allomaison.Repositories.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessageDTO> getMessagesBetweenUsers(Long userId, Long otherUserId, Timestamp beforeTime) {
        if (userId.equals(otherUserId)) return List.of();

        Long user1 = Math.min(userId, otherUserId);
        Long user2 = Math.max(userId, otherUserId);

        Optional<Conversation> convoOpt = conversationRepository.findByUser1IdAndUser2Id(user1, user2);
        if (convoOpt.isEmpty()) return List.of();

        List<ChatMessage> messages = chatMessageRepository
                .findByConversationChatIdAndSentTimeBeforeOrderBySentTimeAsc(convoOpt.get().getChatId(), beforeTime);

        return messages.stream()
                .map(ChatMessageMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ConversationDTO> getAllConversationsForUser(Long userId) {
        List<Conversation> conversations = conversationRepository.findByUser1IdOrUser2Id(userId, userId);

        return conversations.stream()
                .map(convo -> {
                    Long chatId = convo.getChatId();
                    ChatMessage lastMsg = chatMessageRepository
                            .findFirstByConversationChatIdOrderBySentTimeDesc(chatId)
                            .orElse(null);
                    Long lastSenderId = null;
                    Timestamp lastSentTime = null;

                    if (lastMsg != null) {
                        lastSenderId = lastMsg.isSentByUser1() ? convo.getUser1Id() : convo.getUser2Id();
                        lastSentTime = lastMsg.getSentTime();
                    }

                    return ConversationMapper.toDTO(convo, lastSenderId, lastSentTime);
                })
                .collect(Collectors.toList());
    }

    public ChatMessageDTO sendMessage(MessageRequest request) {
        Long senderId = request.senderId();
        Long receiverId = request.receiverId();

        if (senderId.equals(receiverId)) throw new IllegalArgumentException("Sender and receiver cannot be the same.");

        Long user1 = Math.min(senderId, receiverId);
        Long user2 = Math.max(senderId, receiverId);

        Conversation convo = conversationRepository.findByUser1IdAndUser2Id(user1, user2)
                .orElseGet(() -> {
                    Conversation newConvo = new Conversation();
                    newConvo.setUser1Id(user1);
                    newConvo.setUser2Id(user2);
                    return conversationRepository.save(newConvo);
                });

        ChatMessage msg = new ChatMessage();
        msg.setConversation(convo);
        msg.setSentByUser1(senderId.equals(user1));
        msg.setContent(request.content());
        msg.setSentTime(new Timestamp(System.currentTimeMillis()));

        ChatMessage saved = chatMessageRepository.save(msg);
        return ChatMessageMapper.toDTO(saved);
    }
}

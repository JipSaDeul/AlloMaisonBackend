package com.example.allomaison.Services;

import com.example.allomaison.DTOs.ChatMessageDTO;
import com.example.allomaison.DTOs.Requests.MessageRequest;
import com.example.allomaison.DTOs.Responses.ConversationResponse;
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
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

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

    public List<ConversationResponse> getConversationResponsesForUser(Long userId, Function<Long, String> resolveName) {
        List<Conversation> conversations = conversationRepository.findByUser1IdOrUser2Id(userId, userId);

        return conversations.stream()
                .map(convo -> {
                    ChatMessage lastMsg = chatMessageRepository
                            .findFirstByConversationChatIdOrderBySentTimeDesc(convo.getChatId())
                            .orElse(null);

                    Long contactId = convo.getUser1Id().equals(userId) ? convo.getUser2Id() : convo.getUser1Id();
                    String contactName = resolveName.apply(contactId);

                    return ConversationMapper.toResponse(convo, lastMsg, contactName);
                })
                .toList();
    }

    public Long getOrCreateConversationId(Long senderId, Long receiverId) {
        Long user1 = Math.min(senderId, receiverId);
        Long user2 = Math.max(senderId, receiverId);

        return conversationRepository.findByUser1IdAndUser2Id(user1, user2)
                .orElseGet(() -> {
                    Conversation newConvo = new Conversation();
                    newConvo.setUser1Id(user1);
                    newConvo.setUser2Id(user2);
                    return conversationRepository.save(newConvo);
                }).getChatId();
    }
    public List<ChatMessageDTO> getMessagesByChatId(Long chatId) {
        return getMessagesByChatId(chatId, new Timestamp(System.currentTimeMillis()));
    }

    public List<ChatMessageDTO> getMessagesByChatId(Long chatId, Timestamp beforeTime) {
        return conversationRepository.findById(chatId)
                .map(convo -> chatMessageRepository
                        .findByConversationChatIdAndSentTimeBeforeOrderBySentTimeAsc(chatId, beforeTime)
                        .stream()
                        .map(ChatMessageMapper::toDTO)
                        .toList())
                .orElse(List.of());
    }

}

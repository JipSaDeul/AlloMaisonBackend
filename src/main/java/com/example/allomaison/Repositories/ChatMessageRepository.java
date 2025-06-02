package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByConversationChatIdAndSentTimeBeforeOrderBySentTimeAsc(Long chatId, Timestamp before);
    Optional<ChatMessage> findFirstByConversationChatIdOrderBySentTimeDesc(Long chatId);
}

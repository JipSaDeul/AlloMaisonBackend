package com.example.allomaison.Repositories;

import com.example.allomaison.Entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);
    List<Conversation> findByUser1IdOrUser2Id(Long user1Id, Long user2Id);
}
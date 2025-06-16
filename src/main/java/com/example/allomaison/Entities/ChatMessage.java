package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "ChatMessages")
@Data
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatId", nullable = false)
    private Conversation conversation;

    @Column(nullable = false)
    private boolean sentByUser1;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp sentTime;

}

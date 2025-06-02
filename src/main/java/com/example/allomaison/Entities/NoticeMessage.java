package com.example.allomaison.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "NoticeMessages")
@Data
public class NoticeMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    private Long userId; // nullable, system-wide if null

    @Column(nullable = false, length = 64)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type = Type.NOTICE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Target targets = Target.ALL;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp sentTime;

    public enum Type {
        NOTICE, SYSTEM, WARNING
    }

    public enum Target {
        ALL, PROVIDERS, PERSONAL
    }
}

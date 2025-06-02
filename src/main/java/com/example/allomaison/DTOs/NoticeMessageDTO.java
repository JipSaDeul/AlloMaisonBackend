package com.example.allomaison.DTOs;

import com.example.allomaison.Entities.NoticeMessage.Target;
import com.example.allomaison.Entities.NoticeMessage.Type;

import java.sql.Timestamp;

public record NoticeMessageDTO(
        Long noticeId,
        Long userId,
        String title,
        String content,
        Type type,
        Target targets,
        Timestamp sentTime
) {}

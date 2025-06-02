package com.example.allomaison.DTOs;

import com.example.allomaison.Entities.NoticeMessage;
import lombok.Data;

@Data
public class NoticeRequest {
    private Long userId; // null for system notices
    private String title;
    private String content;
    private NoticeMessage.Type type = NoticeMessage.Type.NOTICE;
    private NoticeMessage.Target targets = NoticeMessage.Target.ALL;
}

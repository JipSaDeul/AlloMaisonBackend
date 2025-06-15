package com.example.allomaison.DTOs.Projections;

import com.example.allomaison.Entities.NoticeMessage;

import java.sql.Timestamp;

public interface NoticeProjection {
    Long getNoticeId();
    Long getUserId();
    String getTitle();
    String getContent();
    NoticeMessage.Type getType();
    NoticeMessage.Target getTargets();
    Timestamp getSentTime();
    Boolean getRecent();
}


package com.example.allomaison.Mapper;

import com.example.allomaison.DTOs.NoticeMessageDTO;
import com.example.allomaison.Entities.NoticeMessage;

public class NoticeMessageMapper {
    public static NoticeMessageDTO toDTO(NoticeMessage msg) {
        return new NoticeMessageDTO(
                msg.getNoticeId(),
                msg.getUserId(),
                msg.getTitle(),
                msg.getContent(),
                msg.getType(),
                msg.getTargets(),
                msg.getSentTime()
        );
    }
}

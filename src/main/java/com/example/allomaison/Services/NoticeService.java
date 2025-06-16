package com.example.allomaison.Services;

import com.example.allomaison.DTOs.NoticeMessageDTO;
import com.example.allomaison.DTOs.Projections.NoticeProjection;
import com.example.allomaison.DTOs.Requests.NoticeRequest;
import com.example.allomaison.DTOs.UserDTO;
import com.example.allomaison.Entities.NoticeMessage;
import com.example.allomaison.Repositories.NoticeMessageRepository;
import com.example.allomaison.Repositories.ProviderInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMessageRepository noticeMessageRepository;
    private final ProviderInfoRepository providerInfoRepository;

    public List<Pair<NoticeMessageDTO, Boolean>> getNoticesForUser(UserDTO userDTO) {
        boolean isProvider = providerInfoRepository.existsById(userDTO.getUserId());

        List<NoticeProjection> projections = noticeMessageRepository.findRelevantNotices(
                userDTO.getUserId(),
                isProvider,
                userDTO.getLastLoginTime()
        );

        return projections.stream()
                .map(p -> Pair.of(
                        new NoticeMessageDTO(
                                p.getNoticeId(),
                                p.getUserId(),
                                p.getTitle(),
                                p.getContent(),
                                p.getType(),
                                p.getTargets(),
                                p.getSentTime()
                        ),
                        p.getSentTime().after(userDTO.getLastLoginTime())
                ))
                .toList();
    }

    public Optional<NoticeMessageDTO> postNotice(NoticeRequest request) {
        NoticeMessage notice = new NoticeMessage();

        if (request.getTargets() == NoticeMessage.Target.PERSONAL) {
            if (request.getUserId() == null) {
                return Optional.empty(); // User ID must be set for PERSONAL target
            }
            notice.setUserId(request.getUserId());
        } else {
            notice.setUserId(null); // For ALL and PROVIDERS, userId is not set
        }

        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        notice.setType(request.getType());
        notice.setTargets(request.getTargets());

        NoticeMessage saved = noticeMessageRepository.save(notice);

        return Optional.of(new NoticeMessageDTO(
                saved.getNoticeId(),
                saved.getUserId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getType(),
                saved.getTargets(),
                saved.getSentTime()
        ));
    }

    public List<NoticeMessageDTO> getAllSystemNotices() {
        return noticeMessageRepository.findByUserIdIsNull().stream()
                .map(msg -> new NoticeMessageDTO(
                        msg.getNoticeId(),
                        msg.getUserId(),
                        msg.getTitle(),
                        msg.getContent(),
                        msg.getType(),
                        msg.getTargets(),
                        msg.getSentTime()
                ))
                .toList();
    }

    public List<NoticeMessageDTO> getAllProviderNotices() {
        return noticeMessageRepository.findByTargets(NoticeMessage.Target.PROVIDERS).stream()
                .map(msg -> new NoticeMessageDTO(
                        msg.getNoticeId(),
                        msg.getUserId(),
                        msg.getTitle(),
                        msg.getContent(),
                        msg.getType(),
                        msg.getTargets(),
                        msg.getSentTime()
                ))
                .toList();
    }

    public List<NoticeMessageDTO> getAllGlobalNotices() {
        return noticeMessageRepository.findByTargets(NoticeMessage.Target.ALL).stream()
                .map(msg -> new NoticeMessageDTO(
                        msg.getNoticeId(),
                        msg.getUserId(),
                        msg.getTitle(),
                        msg.getContent(),
                        msg.getType(),
                        msg.getTargets(),
                        msg.getSentTime()
                ))
                .toList();
    }
}

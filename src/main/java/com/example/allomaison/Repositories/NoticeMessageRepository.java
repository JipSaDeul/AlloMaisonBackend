package com.example.allomaison.Repositories;

import com.example.allomaison.DTOs.Projections.NoticeProjection;
import com.example.allomaison.Entities.NoticeMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface NoticeMessageRepository extends JpaRepository<NoticeMessage, Long> {

    List<NoticeMessage> findByUserIdIsNull();

    List<NoticeMessage> findByTargets(NoticeMessage.Target target);

    @Query("""
            SELECT n.noticeId AS noticeId,
                        n.userId AS userId,
                        n.title AS title,
                        n.content AS content,
                        n.type AS type,
                        n.targets AS targets,
                        n.sentTime AS sentTime,
                        (n.sentTime > :lastLoginTime) AS recent
                 FROM NoticeMessage n
                 WHERE
                     (n.targets = 'ALL')
                     OR (n.targets = 'PROVIDERS' AND :isProvider = true)
                     OR (n.targets = 'PERSONAL' AND n.userId = :userId)
                 ORDER BY n.sentTime DESC
            """)
    List<NoticeProjection> findRelevantNotices(
            @Param("userId") Long userId,
            @Param("isProvider") boolean isProvider,
            @Param("lastLoginTime") Timestamp lastLoginTime
    );
}

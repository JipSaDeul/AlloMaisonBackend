-- approval notification
INSERT INTO `Multilingual_tags` (tag, language, content)
VALUES ('notice.approve.title', 'ENGLISH', 'Approval Notification'),
       ('notice.approve.title', 'CHINESE_MANDARIN', '申请通过通知'),
       ('notice.approve.title', 'JOSEONJOK_MAL', '승인 알림'),
       ('notice.approve.title', 'FRENCH', 'Notification d’approbation'),

       ('notice.approve.content', 'ENGLISH', 'Your provider application has been approved. Welcome aboard!'),
       ('notice.approve.content', 'CHINESE_MANDARIN', '您的家政服务者申请已通过，欢迎加入平台！'),
       ('notice.approve.content', 'JOSEONJOK_MAL', '당신의 가정봉사 일꾼 신청이 승인되었습니다. 환영합니다!'),
       ('notice.approve.content', 'FRENCH',
        'Votre demande d’inscription en tant que prestataire a été approuvée. Bienvenue parmi nous!');

-- rejection notification
INSERT INTO `Multilingual_tags` (tag, language, content)
VALUES ('notice.reject.title', 'ENGLISH', 'Application Rejected'),
       ('notice.reject.title', 'CHINESE_MANDARIN', '申请未通过通知'),
       ('notice.reject.title', 'JOSEONJOK_MAL', '신청 거절 알림'),
       ('notice.reject.title', 'FRENCH', 'Demande refusée'),

       ('notice.reject.content', 'ENGLISH',
        'Unfortunately, your provider application was not approved. Please contact support if you have questions.'),
       ('notice.reject.content', 'CHINESE_MANDARIN', '很抱歉，您的家政服务者申请未通过审核。如有疑问请联系客服。'),
       ('notice.reject.content', 'JOSEONJOK_MAL', '죄송합니다. 당신의 신청이 승인되지 않았습니다. 궁금한 사항은 고객봉사부 문의하세요.'),
       ('notice.reject.content', 'FRENCH',
        'Malheureusement, votre demande d’inscription en tant que prestataire a été refusée. Veuillez contacter le support si vous avez des questions.');

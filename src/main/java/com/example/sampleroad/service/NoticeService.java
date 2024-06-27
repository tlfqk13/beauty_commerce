package com.example.sampleroad.service;

import com.example.sampleroad.domain.Notice;
import com.example.sampleroad.domain.NoticeType;
import com.example.sampleroad.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public String getNotice(NoticeType noticeType) {
        Optional<Notice> notice = noticeRepository.findByNoticeType(noticeType);
        return notice.map(Notice::getContent).orElse(null);
    }
}

package com.example.sampleroad.repository;

import com.example.sampleroad.domain.Notice;
import com.example.sampleroad.domain.NoticeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice,Long> {
    Optional<Notice> findByNoticeType(NoticeType noticeType);
}

package com.example.sampleroad.repository.notification;

import com.example.sampleroad.domain.push.NotificationAgree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationAgreeRepository extends JpaRepository<NotificationAgree,Long>, NotificationAgreeRepositoryCustom {
    Optional<NotificationAgree> findByMemberId(Long memberId);
}

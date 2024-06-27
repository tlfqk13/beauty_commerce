package com.example.sampleroad.repository.notification;

import com.example.sampleroad.domain.notify.WeeklyNotify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeeklyNotificationRepository extends JpaRepository<WeeklyNotify,Long> {

    Optional<WeeklyNotify> findByMemberId(Long memberId);
}

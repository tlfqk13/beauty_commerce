package com.example.sampleroad.repository.notification;

import com.example.sampleroad.domain.SkinType;
import com.example.sampleroad.domain.push.PushType;
import com.example.sampleroad.dto.response.push.NotificationResponseQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationAgreeRepositoryCustom {
    NotificationResponseQueryDto findByMemberIdQueryDsl(Long memberId);

    Page<NotificationResponseQueryDto> findByPushType(PushType pushType, Pageable pageable);

    Page<NotificationResponseQueryDto> findByPushTypeAndMemberIds(PushType pushType, Pageable pageable, List<Long> memberIds);

    boolean existsByMemberId(Long memberId);

    boolean existsByMemberIdAndIsFirst(Long memberId, boolean isFirst);

    Page<NotificationResponseQueryDto> findWithoutPurchaseHistory(Pageable pageable1);

    Page<NotificationResponseQueryDto> findHasCart(Pageable pageable);

    Page<NotificationResponseQueryDto> findBySkinCondition(Pageable pageable, SkinType skinType);
}

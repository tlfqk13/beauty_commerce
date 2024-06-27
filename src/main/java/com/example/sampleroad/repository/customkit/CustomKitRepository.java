package com.example.sampleroad.repository.customkit;

import com.example.sampleroad.domain.customkit.CustomKit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomKitRepository extends JpaRepository<CustomKit, Long>, CustomKitRepositoryCustom {

    Optional<CustomKit> findByOrderSheetNo(String orderSheetNo);

    Optional<CustomKit> findByIsOrderedAndMemberId(boolean isOrdered, Long id);

    Optional<CustomKit> findFirstByIsOrderedAndMemberIdAndOrderNoIsNullOrderByCreatedAtDesc(boolean isOrdered, Long id);

    Optional<CustomKit> findByIsOrderedAndMemberIdAndOrderNoIsNull(boolean isOrdered, Long id);

    Optional<CustomKit> findByMemberIdAndIsOrdered(Long memberId, boolean isOrdered);

    Optional<CustomKit> findByMemberIdAndIsOrderedAndOrderNo(Long memberId, boolean isOrdered, String orderNo);
}

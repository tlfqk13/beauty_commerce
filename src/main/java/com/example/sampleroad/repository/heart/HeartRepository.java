package com.example.sampleroad.repository.heart;

import com.example.sampleroad.domain.Heart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart,Long>, HeartRepositoryCustom {
    Optional<Heart> findByReviewIdAndMemberId(Long reviewId, Long memberId);
    Optional<Heart> findByMember_IdAndReviewReviewNo(Long memberId, int reviewNo);
}


package com.example.sampleroad.repository.heart;

import com.example.sampleroad.dto.response.HeartQueryDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HeartRepositoryCustom {
    List<HeartQueryDto.HeartWithReview> findByMemberIdInAndReviewNoIn(Long memberId, List<Integer> reviewNos);
    boolean existsByMemberIdAndReviewNo(Long id, int reviewNo);
    Set<Long> existsByMemberIdAndReviewNoIn(Long id, Set<Integer> reviewNos, int productNo);
}

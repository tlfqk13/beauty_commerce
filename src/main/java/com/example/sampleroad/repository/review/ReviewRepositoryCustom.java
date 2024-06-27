package com.example.sampleroad.repository.review;

import com.example.sampleroad.dto.response.review.ReviewQueryDto;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ReviewRepositoryCustom {
    ReviewQueryDto.ReviewWithSurvey findReviewWithSurvey(int reviewNo, String registerNo);
    Page<ReviewQueryDto.ReviewInfo> findReviewInfoByProductNo(Pageable pageable, int productNo);
    Double findReviewRateAvg(int productNo);
    boolean existsByReviewNo(int reviewNo);
    Map<Integer, Integer> findReviewsInfoCount(Set<Integer> productNos);
    ReviewQueryDto.ReviewInfo findReviewInfoByProductNo(int reviewNo, int productNo);
    List<ReviewQueryDto.ReviewInfo> findByOrderOptionNosAndMemberId(List<Long> orderOptionNos, Long memberId);
    Tuple findReviewCountAndRateAvg(int productNo);
    boolean existsSampleRoadReviewByProductNo(int productNo);
}

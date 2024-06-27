package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.Heart;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.review.Review;
import com.example.sampleroad.dto.response.HeartQueryDto;
import com.example.sampleroad.repository.heart.HeartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReviewHeartService {

    private final HeartRepository heartRepository;

    @Transactional
    public void addHeartByReviewMember(Review review, Member member) {

        heartRepository.findByReviewIdAndMemberId(review.getId(), member.getId())
                .ifPresent(heart -> {
                    throw new ErrorCustomException(ErrorCode.ALREADY_HEART_REVIEW);
                });

        Heart heart = Heart.builder()
                .member(member)
                .review(review)
                .build();

        heartRepository.save(heart);
    }

    @Transactional
    public void deleteHeartByReviewMember(Review review, Member member) {

        Heart heart = heartRepository.findByReviewIdAndMemberId(review.getId(), member.getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.REVIEW_NOT_FOUND));

        heartRepository.delete(heart);
    }

    public boolean existsByMemberIdAndReviewNo(Long memberId, int reviewNo) {
        return heartRepository.existsByMemberIdAndReviewNo(memberId, reviewNo);
    }

    public Set<Long> existsByMemberIdAndReviewNoIn(Long memberId, Set<Integer> reviewNosFromReviewInfos, int productNo) {
        return heartRepository.existsByMemberIdAndReviewNoIn(memberId, reviewNosFromReviewInfos, productNo);
    }

    public Set<Long> findByMemberIdInAndReviewNoIn(Long memberId, List<Integer> reviewNoList) {
       return heartRepository.findByMemberIdInAndReviewNoIn(memberId, reviewNoList).stream()
                .filter(heartWithReview -> Objects.equals(heartWithReview.getMemberId(), memberId))
                .map(HeartQueryDto.HeartWithReview::getReviewId)
                .collect(Collectors.toSet());
    }
}

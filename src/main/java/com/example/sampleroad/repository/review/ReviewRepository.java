package com.example.sampleroad.repository.review;

import com.example.sampleroad.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    Optional<Review> findByReviewNo(int reviewNo);
    List<Review> findAllByMemberIdAndReviewNoInOrderByIdDesc(Long id, List<Integer> reviewNoList);
}

package com.example.sampleroad.repository.review;

import com.example.sampleroad.domain.review.ReviewCrawling;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewCrawlingRepository extends JpaRepository<ReviewCrawling,Long> {

    Optional<ReviewCrawling> findByProductNo(int productNo);

}

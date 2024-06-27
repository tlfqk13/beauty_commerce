package com.example.sampleroad.repository.review;

import com.example.sampleroad.dto.response.review.ReviewPhotoQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewPhotoRepositoryCustom {
    Page<ReviewPhotoQueryDto.ReviewPhoto> findReviewPhotoByProductNo(int productNo, Pageable pageable);
    List<ReviewPhotoQueryDto.ReviewPhoto> findReviewPhotoByProductNo(int productNo);
    List<ReviewPhotoQueryDto.ReviewPhoto> findReviewPhotoByReviewNo(int reviewNo, Long memberId);
    List<ReviewPhotoQueryDto.ReviewPhoto> findReviewPhotoByReviewNo(int reviewNo);
}

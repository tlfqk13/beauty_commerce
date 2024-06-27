package com.example.sampleroad.dto.response.review;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewPhotoQueryDto {

    @NoArgsConstructor
    @Getter
    public static class ReviewPhoto {
        private Long reviewId;
        private int reviewNo;
        private int productNo;
        private int reviewRecommendCnt;
        private String reviewImgUrl;

        @QueryProjection
        public ReviewPhoto(Long reviewId, int reviewNo, int productNo, int reviewRecommendCnt, String reviewImgUrl) {
            this.reviewId = reviewId;
            this.reviewNo = reviewNo;
            this.productNo = productNo;
            this.reviewRecommendCnt = reviewRecommendCnt;
            this.reviewImgUrl = reviewImgUrl;
        }
    }
}

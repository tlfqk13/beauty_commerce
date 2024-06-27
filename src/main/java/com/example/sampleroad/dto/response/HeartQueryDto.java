package com.example.sampleroad.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class HeartQueryDto {
    @NoArgsConstructor
    @Getter
    public static class HeartWithReview{
        private Long memberId;
        private Long reviewId;
        private Long heartId;
        private int reviewNo;

        @QueryProjection
        public HeartWithReview(Long memberId, Long reviewId,Long heartId, int reviewNo) {
            this.memberId = memberId;
            this.reviewId = reviewId;
            this.heartId = heartId;
            this.reviewNo = reviewNo;
        }
    }
}

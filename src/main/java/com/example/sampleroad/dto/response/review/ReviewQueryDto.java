package com.example.sampleroad.dto.response.review;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewQueryDto {

    @NoArgsConstructor
    @Getter
    public static class ReviewWithSurvey {
        private int reviewNo;
        private Long memberId;
        private String reviewTag;
        private String skinTrouble;
        private String profileImage;
        private String nickName;
        private String skinType;

        @QueryProjection
        public ReviewWithSurvey(int reviewNo, Long memberId, String reviewTag, String skinTrouble,
                                String profileImage, String nickName, String skinType) {
            this.reviewNo = reviewNo;
            this.memberId = memberId;
            this.reviewTag = reviewTag;
            this.skinTrouble = skinTrouble;
            this.profileImage = profileImage;
            this.nickName = nickName;
            this.skinType = skinType;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ReviewInfo {
        private Long reviewId;
        private String memberNo;
        private int reviewNo;
        private Long productId;
        private String productImgUrl;
        private String brandName;
        private int productNo;
        private String registerDate;
        private String content;
        private double reviewRate;
        private int recommendCnt;
        private String productName;
        private String tag;
        private Long memberId;
        private String nickName;

        @QueryProjection
        public ReviewInfo(Long reviewId, String memberNo, int reviewNo, Long productId, String productImgUrl,
                          String brandName, int productNo, String registerDate,
                          String content, double reviewRate, int recommendCnt,
                          String productName, String tag,
                          Long memberId,String nickName) {
            this.reviewId = reviewId;
            this.memberNo = memberNo;
            this.reviewNo = reviewNo;
            this.productId = productId;
            this.productImgUrl = productImgUrl;
            this.brandName = brandName;
            this.productNo = productNo;
            this.registerDate = registerDate;
            this.content = content;
            this.reviewRate = reviewRate;
            this.recommendCnt = recommendCnt;
            this.productName = productName;
            this.tag = tag;
            this.memberId = memberId;
            this.nickName = nickName;
        }

        @QueryProjection
        public ReviewInfo(Long reviewId, int reviewNo, Long productId, String productImgUrl,
                          String brandName, int productNo, String registerDate,
                          String content, double reviewRate, int recommendCnt,
                          String productName, String tag,
                          Long memberId) {
            this.reviewId = reviewId;
            this.reviewNo = reviewNo;
            this.productId = productId;
            this.productImgUrl = productImgUrl;
            this.brandName = brandName;
            this.productNo = productNo;
            this.registerDate = registerDate;
            this.content = content;
            this.reviewRate = reviewRate;
            this.recommendCnt = recommendCnt;
            this.productName = productName;
            this.tag = tag;
            this.memberId = memberId;
        }
    }
}

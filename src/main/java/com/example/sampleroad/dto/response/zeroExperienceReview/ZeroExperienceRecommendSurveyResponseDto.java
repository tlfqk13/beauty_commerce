package com.example.sampleroad.dto.response.zeroExperienceReview;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ZeroExperienceRecommendSurveyResponseDto {

    private Long totalCount;
    private Integer displayNo;
    private String noticeImageUrl;
    private List<ItemInfo> items;

    public ZeroExperienceRecommendSurveyResponseDto(Long totalCount, Integer displayNo, String noticeImageUrl, List<ItemInfo> items) {
        this.totalCount = totalCount;
        this.displayNo = displayNo;
        this.noticeImageUrl = noticeImageUrl;
        this.items = items;
    }

    @NoArgsConstructor
    @Getter
    public static class ItemInfo {
        private Long recommendSurveyId;
        private Long orderItemId;
        private Long questionSurveyId;
        private String brandName;
        private String imageUrl;
        private String productName;
        private int productNo;
        private String purchaseTime;
        private Boolean isRecommend; // 추천,비추천
        private Boolean hasQuestionSurvey; // 설문 참여 여부
        private String orderNo;
        private Boolean isNecessary; // 필수 여부

        public ItemInfo(Long recommendSurveyId, Long orderItemId, Long questionSurveyId,
                        String brandName, String imageUrl, String productName, int productNo,
                        String purchaseTime,
                        Boolean isRecommend, Boolean hasQuestionSurvey, String orderNo, Boolean isNecessary) {
            this.recommendSurveyId = recommendSurveyId;
            this.orderItemId = orderItemId;
            this.questionSurveyId = questionSurveyId;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
            this.productName = productName;
            this.productNo = productNo;
            this.purchaseTime = purchaseTime;
            this.isRecommend = isRecommend;
            this.hasQuestionSurvey = hasQuestionSurvey;
            this.orderNo =orderNo;
            this.isNecessary = isNecessary;
        }

        public Boolean getIsRecommend() {
            return isRecommend;
        }

        public Boolean getHasQuestionSurvey() {
            return hasQuestionSurvey;
        }

        public Boolean getIsNecessary() {
            return isNecessary;
        }
    }
}

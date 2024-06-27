package com.example.sampleroad.dto.response.zeroExperienceReview;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ZeroExperienceRecommendSurveyQueryDto {
    private Long zeroExperienceId;
    private Long ordersItemId;
    private Long questionSurveyId;
    private Boolean isRecommend;
    private int productNo;
    private String productName;
    private String brandName;
    private String productImageUrl;
    private String orderNo;
    private LocalDateTime productPurchaseTime;

    @QueryProjection
    public ZeroExperienceRecommendSurveyQueryDto(Long zeroExperienceId, Long ordersItemId, Long questionSurveyId,
                                                 Boolean isRecommend,
                                                 int productNo, String productName, String brandName,
                                                 String productImageUrl, String orderNo,
                                                 LocalDateTime productPurchaseTime) {
        this.zeroExperienceId = zeroExperienceId;
        this.ordersItemId = ordersItemId;
        this.questionSurveyId = questionSurveyId;
        this.isRecommend = isRecommend;
        this.productNo = productNo;
        this.productName = productName;
        this.brandName = brandName;
        this.productImageUrl = productImageUrl;
        this.orderNo = orderNo;
        this.productPurchaseTime = productPurchaseTime;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class OrdersItemInfo {
        private Long ordersItemId;
        private int productNo;
        private boolean isNecessary;
        private String productName;
        private String brandName;
        private String productImageUrl;
        private String orderNo;
        private LocalDateTime productPurchaseTime;

        public boolean getIsNecessary() {
            return isNecessary;
        }

        public void setIsNecessary(boolean necessary) {
            isNecessary = necessary;
        }

        public OrdersItemInfo(Long ordersItemId, int productNo, boolean isNecessary, String productName,
                              String brandName, String productImageUrl, String orderNo,
                              LocalDateTime productPurchaseTime) {
            this.ordersItemId = ordersItemId;
            this.productNo = productNo;
            this.isNecessary = isNecessary;
            this.productName = productName;
            this.brandName = brandName;
            this.productImageUrl = productImageUrl;
            this.orderNo = orderNo;
            this.productPurchaseTime = productPurchaseTime;
        }

        @QueryProjection
        public OrdersItemInfo(Long ordersItemId,
                              int productNo, String productName, String brandName,
                              String productImageUrl, String orderNo,
                              LocalDateTime productPurchaseTime) {
            this.ordersItemId = ordersItemId;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.productImageUrl = productImageUrl;
            this.orderNo = orderNo;
            this.productPurchaseTime = productPurchaseTime;
        }
    }
}

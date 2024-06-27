package com.example.sampleroad.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CouponRequestDto {

    @NoArgsConstructor
    @Getter
    public static class PromotionCode {
        private String promotionCode;
    }

    @NoArgsConstructor
    @Getter
    public static class CouponCalculate {
        private int cartCouponIssueNo;
        private String promotionCode;
        private List<ProductCoupons> productCoupons;

    }

    @NoArgsConstructor
    @Getter
    public static class ProductCoupons {
        private int productNo;
        private int couponIssueNo;
    }
}


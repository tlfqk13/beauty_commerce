package com.example.sampleroad.dto.request.order;

import com.example.sampleroad.dto.request.CouponRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class OrderCalculateCouponRequestDto {
    private int cartCouponIssueNo;
    private String promotionCode;
    private List<CouponRequestDto.ProductCoupons> productCoupons;

    @NoArgsConstructor
    @Getter
    public static class ProductCoupons {
        private int productNo;
        private int couponIssueNo;
    }
}

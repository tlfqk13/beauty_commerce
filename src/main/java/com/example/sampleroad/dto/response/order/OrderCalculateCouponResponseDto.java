package com.example.sampleroad.dto.response.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
public class OrderCalculateCouponResponseDto {
    private int totalCount;
    private List<UsableCouponInfo> cartCouponList;
    private List<UsableCouponInfo> productCouponList;


    public OrderCalculateCouponResponseDto(int totalCount, List<UsableCouponInfo> cartCouponList, List<UsableCouponInfo> productCouponList) {
        this.totalCount = totalCount;
        this.cartCouponList = cartCouponList;
        this.productCouponList = productCouponList;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class CalculateCouponResult {

        int cartCouponDiscountAmt;
        int productCouponDiscountAmt;

        public CalculateCouponResult(int cartCouponDiscountAmt, int productCouponDiscountAmt) {
            this.cartCouponDiscountAmt = cartCouponDiscountAmt;
            this.productCouponDiscountAmt = productCouponDiscountAmt;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class UsableCouponInfo {
        private int couponIssueNo;
        private int couponNo;
        private String couponTitle;
        private String couponName;
        private String reason;
        private String couponType;
        private int productNo;
        private int discountRate;
        private int discountAmt;
        private String minSalePriceStr;
        private String dday;
        private String useEndYmdt;
        private String maxDiscountAmtStr;

        public UsableCouponInfo(int couponIssueNo, int couponNo,
                                String couponTitle, String couponName,
                                String reason, String couponType, int productNo,
                                int discountRate, int discountAmt,
                                String minSalePriceStr, String dday, String useEndYmdt,
                                String maxDiscountAmtStr) {
            this.couponIssueNo = couponIssueNo;
            this.couponNo = couponNo;
            this.couponTitle = couponTitle;
            this.couponName = couponName;
            this.reason = reason;
            this.couponType = couponType;
            this.productNo = productNo;
            this.discountRate = discountRate;
            this.discountAmt = discountAmt;
            this.minSalePriceStr = minSalePriceStr;
            this.dday = dday;
            this.useEndYmdt = useEndYmdt;
            this.maxDiscountAmtStr = maxDiscountAmtStr;
        }

        public UsableCouponInfo(int couponIssueNo, int couponNo,
                                String couponTitle, String couponName,
                                String reason, String type,
                                int productNo, int discountRate,
                                String minSalePriceStr,
                                int discountAmt, String dday, String useEndYmdt) {
            this.couponIssueNo = couponIssueNo;
            this.couponNo = couponNo;
            this.couponTitle = couponTitle;
            this.couponName = couponName;
            this.reason = reason;
            this.productNo = productNo;
            this.discountRate = discountRate;
            this.minSalePriceStr = minSalePriceStr;
            this.discountAmt = discountAmt;
            this.dday = dday;
            this.useEndYmdt = useEndYmdt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UsableCouponInfo that = (UsableCouponInfo) o;
            return couponNo == that.couponNo;
        }

        @Override
        public int hashCode() {
            return Objects.hash(couponNo);
        }
    }
}

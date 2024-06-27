package com.example.sampleroad.dto.response.coupon;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CouponResponseDto {

    @NoArgsConstructor
    @Getter
    public static class Coupon {
        private int totalCount;
        private List<? extends BaseCouponInfo> couponInfoList;

        public Coupon(int totalCount, List<? extends BaseCouponInfo> couponInfoList) {
            this.totalCount = totalCount;
            this.couponInfoList = couponInfoList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class AllMyCouponsInfo {
        int totalCount;
        List<DownloadedCouponInfo> couponInfoList;

        public AllMyCouponsInfo(int totalCount, List<DownloadedCouponInfo> couponInfoList) {
            this.totalCount = totalCount;
            this.couponInfoList = couponInfoList;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class BaseCouponInfo {
        protected int couponNo;
        protected String couponTitle;
        protected String couponName;
        protected String couponType;
        protected int discountRate;
        protected int discountAmt;
        protected String minSalePriceStr;
        protected String dday;
    }

    @Getter
    @NoArgsConstructor
    public static class DownloadedCouponInfo extends BaseCouponInfo {
        private int couponIssueNo;
        private String useEndYmdt;
        private String couponTargetType;
        private int minSalePrice;
        private int maxSalePrice;
        private int maxDiscountAmt;
        private String useYmdt;
        private String reason;
        private String maxDiscountAmtStr;

        // DownloadedCouponInfo에만 필요한 생성자와 메서드들...
        public DownloadedCouponInfo(int couponIssueNo, String couponName, int couponNo, String couponType,
                                    int discountAmt, int discountRate, String useEndYmdt, String couponTargetType,
                                    int minSalePrice, int maxSalePrice, int maxDiscountAmt, String useYmdt, String reason,
                                    String dday, String minSalePriceStr, String maxDiscountAmtStr, String couponTitle) {
            this.couponName = couponName;
            this.couponTitle = couponTitle;
            this.couponIssueNo = couponIssueNo;
            this.useEndYmdt = useEndYmdt;
            this.couponTargetType = couponTargetType;
            this.minSalePrice = minSalePrice;
            this.maxSalePrice = maxSalePrice;
            this.maxDiscountAmt = maxDiscountAmt;
            this.useYmdt = useYmdt;
            this.reason = reason;
            this.maxDiscountAmtStr = maxDiscountAmtStr;
            this.couponNo = couponNo;
            this.couponType = couponType;
            this.discountAmt = discountAmt;
            this.discountRate = discountRate;
            this.dday = dday;
            this.minSalePriceStr = minSalePriceStr;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class DownloadAbleCouponInfo extends BaseCouponInfo {
        private String issueStartYmdt;
        private String issueEndYmdt;
        private int useDays;
        private String maxDiscountAmtStr;
        private String couponTargetType;
        private Boolean isDownloadable;

        public DownloadAbleCouponInfo(int couponNo, String couponName, String couponType, String issueStartYmdt,
                                      String issueEndYmdt, int discountRate, int discountAmt, int useDays,
                                      String minSalePriceStr, String dday,
                                      String maxDiscountAmtStr, String couponTitle,
                                      String couponTargetType) {
            this.issueStartYmdt = issueStartYmdt;
            this.issueEndYmdt = issueEndYmdt;
            this.useDays = useDays;
            this.maxDiscountAmtStr = maxDiscountAmtStr;
            this.couponNo = couponNo;
            this.couponName = couponName;
            this.couponType = couponType;
            this.discountRate = discountRate;
            this.discountAmt = discountAmt;
            this.minSalePriceStr = minSalePriceStr;
            this.dday = dday;
            this.couponTitle = couponTitle;
            this.couponTargetType = couponTargetType;
        }

        public DownloadAbleCouponInfo(int couponNo, String couponName, String couponType, String issueStartYmdt,
                                      String issueEndYmdt, int discountRate, int discountAmt, int useDays,
                                      String minSalePriceStr, String dday,
                                      String maxDiscountAmtStr, String couponTitle,
                                      String couponTargetType,boolean isDownloadable) {
            this.issueStartYmdt = issueStartYmdt;
            this.issueEndYmdt = issueEndYmdt;
            this.useDays = useDays;
            this.maxDiscountAmtStr = maxDiscountAmtStr;
            this.couponNo = couponNo;
            this.couponName = couponName;
            this.couponType = couponType;
            this.discountRate = discountRate;
            this.discountAmt = discountAmt;
            this.minSalePriceStr = minSalePriceStr;
            this.dday = dday;
            this.couponTitle = couponTitle;
            this.couponTargetType = couponTargetType;
            this.isDownloadable = isDownloadable;
        }
    }


    @NoArgsConstructor
    @Getter
    public static class DownloadAbleCoupon {
        private int totalCount;
        private List<DownloadAbleCouponInfo> couponInfoList;

        public DownloadAbleCoupon(int totalCount, List<DownloadAbleCouponInfo> couponInfoList) {
            this.totalCount = totalCount;
            this.couponInfoList = couponInfoList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RegisterCouponInfo {
        private int couponIssueNo;
        private int couponNo;
        private String couponName;
        private String useEndYmdt;

        public RegisterCouponInfo(int couponIssueNo, int couponNo, String couponName, String useEndYmdt) {
            this.couponIssueNo = couponIssueNo;
            this.couponNo = couponNo;
            this.couponName = couponName;
            this.useEndYmdt = useEndYmdt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ProductAllCoupon {
        List<CouponResponseDto.IssuedCoupons> issuedCoupons;
        List<CouponResponseDto.IssueFailCoupons> issueFailCoupons;

        public ProductAllCoupon(List<IssuedCoupons> issuedCoupons, List<IssueFailCoupons> issueFailCoupons) {
            this.issuedCoupons = issuedCoupons;
            this.issueFailCoupons = issueFailCoupons;
        }
    }


    @NoArgsConstructor
    @Getter
    public static class IssuedCoupons {
        private int couponIssueNo;
        private int couponNo;
        private String couponName;
        private String useEndYmdt;

        public IssuedCoupons(int couponIssueNo, int couponNo, String couponName, String useEndYmdt) {
            this.couponIssueNo = couponIssueNo;
            this.couponNo = couponNo;
            this.couponName = couponName;
            this.useEndYmdt = useEndYmdt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class IssueFailCoupons {
        private int couponNo;
        private String failMessage;

        public IssueFailCoupons(int couponNo, String failMessage) {
            this.couponNo = couponNo;
            this.failMessage = failMessage;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ProductCouponInfo extends BaseCouponInfo {
        private String issueStartYmdt;
        private String issueEndYmdt;
        private int useDays;
        private String minSalePriceStr;
        private String maxDiscountAmtStr;
        private boolean isIssued;

        public boolean getIsIssued() {
            return isIssued;
        }

        public ProductCouponInfo(int couponNo, String couponTitle, String couponName,
                                 String couponType,
                                 String issueStartYmdt, String issueEndYmdt,
                                 int discountRate, int discountAmt, int useDays,
                                 String minSalePriceStr, String dday,
                                 String maxDiscountAmtStr, boolean isIssued) {

            this.issueStartYmdt = issueStartYmdt;
            this.issueEndYmdt = issueEndYmdt;
            this.useDays = useDays;
            this.maxDiscountAmtStr = maxDiscountAmtStr;
            this.couponNo = couponNo;
            this.couponTitle = couponTitle;
            this.couponName = couponName;
            this.couponType = couponType;
            this.discountRate = discountRate;
            this.discountAmt = discountAmt;
            this.minSalePriceStr = minSalePriceStr;
            this.dday = dday;
            this.isIssued = isIssued;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CouponTargetDto {
        private int targetNo;
        private String targetName;
        private String targetType;

        public CouponTargetDto(int targetNo, String targetName, String targetType) {
            this.targetNo = targetNo;
            this.targetName = targetName;
            this.targetType = targetType;
        }
    }

}

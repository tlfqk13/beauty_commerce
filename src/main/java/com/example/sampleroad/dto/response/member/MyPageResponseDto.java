package com.example.sampleroad.dto.response.member;

import com.example.sampleroad.dto.response.banner.BannerResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MyPageResponseDto {

    private boolean hasCart;

    private Point point;

    private Boolean isHaveCoupon;

    private BannerResponseDto bannerList;
    private String versionInfo;

    public MyPageResponseDto(boolean hasCart, Point shopByPoint, Boolean isHaveCoupon,
                             BannerResponseDto bannerList, String versionInfo) {
        this.hasCart = hasCart;
        this.point = shopByPoint;
        this.isHaveCoupon = isHaveCoupon;
        this.bannerList = bannerList;
        this.versionInfo = versionInfo;
    }

    @NoArgsConstructor
    @Getter
    public static class Point {
        private Integer availablePointAmt;

        public Point(Integer availablePointAmt) {
            this.availablePointAmt = availablePointAmt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Coupon {
        private Integer usableCouponCnt;

        public Coupon(Integer usableCouponCnt) {
            this.usableCouponCnt = usableCouponCnt;
        }
    }

}

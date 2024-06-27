package com.example.sampleroad.domain.banner;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "BANNER_DETAIL_COUPON")
@DiscriminatorValue("COUPON")
public class BannerDetailCoupon extends BannerDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANNER_DETAIL_COUPON_ID")
    private Long id;

    @Column(name = "BANNER_DETAIL_COUPON_NO")
    private int bannerDetailCouponNo;

}

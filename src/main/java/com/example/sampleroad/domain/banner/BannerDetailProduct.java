package com.example.sampleroad.domain.banner;

import javax.persistence.*;

@Entity
@Table(name = "BANNER_DETAIL_PRODUCT")
@DiscriminatorValue("PRODUCT")
public class BannerDetailProduct extends BannerDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANNER_DETAIL_PRODUCT_ID")
    private Long id;

    @Column(name = "BANNER_DETAIL_PRODUCT_NO")
    private int bannerDetailProductNo;
}

package com.example.sampleroad.domain.banner;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "BANNER_DETAIL_CATEGORY")
@DiscriminatorValue("CATEGORY")
public class BannerDetailCategory extends BannerDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANNER_DETAIL_CATEGORY_ID")
    private Long id;

    @Column(name = "BANNER_DETAIL_CATEGORY_NO")
    private int bannerDetailCategoryNo;

}

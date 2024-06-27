package com.example.sampleroad.domain.banner;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "BANNER_DETAIL_OTHER")
@DiscriminatorValue("OTHER")
public class BannerDetailOther extends BannerDetail {

    // 리뷰텝, 마이탭, 외부공지,  -> bannerKeyNo가 null 이여도 상관없는

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANNER_DETAIL_OTHER_ID")
    private Long id;

    @Column(name = "BANNER_DETAIL_OTHER_BUTTON_NAME")
    private String bannerDetailOtherButtonName;

}

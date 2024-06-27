package com.example.sampleroad.domain.banner;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED) // 상속 구현 전략 선택
@DiscriminatorColumn(name = "dtype")
public class BannerDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANNER_DETAIL_ID")
    private Long id;

    @Column(name = "BANNER_EXTERNAL_URL")
    private String externalUrl;

    @Column(name = "BANNER_DETAIL_BUTTON_NAME")
    private String bannerDetailButtonName;

    @Column(name = "BANNER_DETAIL_KEY_NO")
    private int bannerKeyNo;

    @Column(name = "BANNER_DETAIL_KEY_STR")
    // TODO: 1/22/24 배너에서 카테고리 이동시 sortType등...
    private String bannerKeyStr;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Banner_ID")
    private Banner banner;
}


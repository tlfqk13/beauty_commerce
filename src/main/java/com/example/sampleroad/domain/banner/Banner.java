package com.example.sampleroad.domain.banner;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "BANNER")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANNER_ID")
    private Long id;

    @Column(name = "BANNER_NAME")
    private String bannerName;

    @Column(name = "IMAGE_URL")
    @Lob
    private String imageUrl;

    @Column(name = "SHOW_NUMBER")
    private int showNumber;

    @Column(name = "IS_VISIBLE")
    private boolean isVisible;

    @Column(name = "BANNER_VIEW_COUNT")
    private int bannerViewCount;

    @Column(name = "BANNER_TYPE")
    @Enumerated(EnumType.STRING)
    private BannerType bannerType;

    @Column(name = "IS_MOVE_BANNERDETAIL")
    private boolean isMoveBannerDetail;

    @Column(name = "BANNER_HEIGHT_RATIO")
    private double heightRatio;

    @Column(name = "BANNER_WIDTH_RATIO")
    private double widthRatio;

    @Column(name = "BANNER_SECTION")
    @Enumerated(EnumType.STRING)
    private BannerSectionType bannerSection;

    public boolean getIsVisible() {
        return isVisible;
    }

    public boolean getIsMoveBannerDetail() {
        return isMoveBannerDetail;
    }

    public void updateBannerViewCount() {
        this.bannerViewCount += 1;
    }
}

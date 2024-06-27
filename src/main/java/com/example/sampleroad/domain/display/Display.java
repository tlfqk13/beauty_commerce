package com.example.sampleroad.domain.display;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "DISPLAY")
public class Display {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_ID")
    private Long id;

    @Column(name = "DISPLAY_MAIN_IMAGE_URL")
    private String displayMainImageUrl;

    @Column(name = "DISPLAY_BANNER_IMAGE_URL")
    private String displayBannerImageUrl;

    @Column(name = "IS_VISIBLE")
    private boolean isVisible;

    @Column(name = "EXPERIENCE_VIEW_COUNT")
    private int displayViewCount;

    @Column(name = "DISPLAY_DESIGN_TYPE")
    @Enumerated(EnumType.STRING)
    private DisplayDesignType displayDesignType;

    @Column(name = "DISPLAY_TYPE")
    @Enumerated(EnumType.STRING)
    private DisplayType displayType;

    @Column(name = "HAS_COUPON")
    private boolean hasCoupon;

    public boolean getIsVisible() {
        return isVisible;
    }

    @Column(name = "DISPLAY_NO")
    private int displayNo;

    public void updateDisplayViewCount() {
        this.displayViewCount += 1;
    }
}

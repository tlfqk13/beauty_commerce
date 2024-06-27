package com.example.sampleroad.domain.banner;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "BANNER_IMAGE")
public class BannerDetailImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANNER_DETAIL_IMAGE_ID")
    private Long id;

    @Column(name = "BANNER_DETAIL_IMG")
    private String bannerDetailImg;

    @Column(name = "BANNER_TYPE")
    @Enumerated(EnumType.STRING)
    private BannerType bannerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Banner_ID")
    private Banner banner;


}

package com.example.sampleroad.domain.banner;

import javax.persistence.*;

@Entity
@Table(name = "BANNER_DETAIL_NOTICE")
@DiscriminatorValue("NOTICE")
public class BannerDetailNotice extends BannerDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BANNER_DETAIL_NOTICE_ID")
    private Long id;

    @Column(name = "BANNER_DETAIL_NOTICE_NO")
    private int bannerDetailNoticeNo;
}

package com.example.sampleroad.repository.banner;

import com.example.sampleroad.domain.banner.BannerSectionType;
import com.example.sampleroad.dto.response.banner.BannerResponseDto;
import com.example.sampleroad.dto.response.banner.QBannerResponseDto_BannerInfoQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.banner.QBanner.banner;
import static com.example.sampleroad.domain.banner.QBannerDetail.bannerDetail;

public class BannerRepositoryImpl implements BannerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BannerRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<BannerResponseDto.BannerInfoQueryDto> findByBannerSection(boolean isVisible, List<BannerSectionType> bannerSectionType) {
        return queryFactory
                .select(new QBannerResponseDto_BannerInfoQueryDto(
                        banner.bannerSection,
                        banner.id,
                        banner.bannerName,
                        banner.imageUrl,
                        bannerDetail.bannerKeyNo,
                        banner.isMoveBannerDetail,
                        banner.bannerType,
                        banner.heightRatio,
                        banner.widthRatio,
                        bannerDetail.bannerKeyStr
                ))
                .from(bannerDetail)
                .rightJoin(bannerDetail.banner, banner)
                .where(banner.isVisible.isTrue()
                        .and(banner.bannerSection.in(bannerSectionType)))
                .orderBy(banner.showNumber.asc())
                .fetch();
    }

}

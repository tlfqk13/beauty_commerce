package com.example.sampleroad.repository.banner;

import com.example.sampleroad.domain.banner.BannerSectionType;
import com.example.sampleroad.domain.banner.BannerType;
import com.example.sampleroad.dto.response.banner.BannerDetailResponseDto;
import com.example.sampleroad.dto.response.banner.QBannerDetailResponseDto_BannerDetailResponse;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.banner.QBanner.banner;
import static com.example.sampleroad.domain.banner.QBannerDetail.bannerDetail;
import static com.example.sampleroad.domain.banner.QBannerDetailCategory.bannerDetailCategory;
import static com.example.sampleroad.domain.banner.QBannerDetailCoupon.bannerDetailCoupon;
import static com.example.sampleroad.domain.banner.QBannerDetailNotice.bannerDetailNotice;
import static com.example.sampleroad.domain.banner.QBannerDetailOther.bannerDetailOther;
import static com.example.sampleroad.domain.banner.QBannerDetailProduct.bannerDetailProduct;

public class BannerDetailRepositoryImpl implements BannerDetailRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public BannerDetailRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public BannerDetailResponseDto.BannerDetailResponse findBannerCouponDetailByBannerId(BannerType bannerType, Long bannerId) {

        return queryFactory
                .select(new QBannerDetailResponseDto_BannerDetailResponse(
                        banner.id,
                        bannerDetail.externalUrl,
                        bannerDetail.bannerDetailButtonName,
                        bannerDetailCoupon.bannerDetailCouponNo,
                        banner.bannerType,
                        banner.bannerName
                ))
                .from(bannerDetail)
                .innerJoin(bannerDetail.banner, banner)
                .leftJoin(bannerDetailCoupon).on(bannerDetailCoupon.banner.id.eq(banner.id))
                .where(banner.id.eq(bannerId)
                        .and(banner.bannerType.in(bannerType))
                        .and(banner.isVisible.isTrue()))
                .fetchOne();
    }

    @Override
    public BannerDetailResponseDto.BannerDetailResponse findBannerProductDetailByBannerId(BannerType bannerType, Long bannerId) {
        return queryFactory
                .select(new QBannerDetailResponseDto_BannerDetailResponse(
                        banner.id,
                        bannerDetail.externalUrl,
                        bannerDetail.bannerDetailButtonName,
                        bannerDetailProduct.bannerDetailProductNo,
                        banner.bannerType,
                        banner.bannerName
                ))
                .from(bannerDetail)
                .innerJoin(bannerDetail.banner, banner)
                .leftJoin(bannerDetailProduct).on(bannerDetailProduct.banner.id.eq(banner.id))
                .where(banner.id.eq(bannerId)
                        .and(banner.bannerType.in(bannerType))
                        .and(banner.isVisible.isTrue()))
                .fetchOne();
    }

    @Override
    public BannerDetailResponseDto.BannerDetailResponse findBannerNoticeDetailByBannerId(BannerType bannerType, Long bannerId) {
        return queryFactory
                .select(new QBannerDetailResponseDto_BannerDetailResponse(
                        banner.id,
                        bannerDetail.externalUrl,
                        bannerDetail.bannerDetailButtonName,
                        bannerDetailNotice.bannerDetailNoticeNo,
                        banner.bannerType,
                        banner.bannerName
                ))
                .from(bannerDetail)
                .innerJoin(bannerDetail.banner, banner)
                .leftJoin(bannerDetailNotice).on(bannerDetailNotice.banner.id.eq(banner.id))
                .where(banner.id.eq(bannerId)
                        .and(banner.bannerType.in(bannerType))
                        .and(banner.isVisible.isTrue()))
                .fetchOne();
    }

    @Override
    public BannerDetailResponseDto.BannerDetailResponse findBannerCategoryDetailByBannerId(BannerType bannerType, Long bannerId) {
        return queryFactory
                .select(new QBannerDetailResponseDto_BannerDetailResponse(
                        banner.id,
                        bannerDetail.externalUrl,
                        bannerDetail.bannerDetailButtonName,
                        bannerDetailCategory.bannerDetailCategoryNo,
                        banner.bannerType,
                        banner.bannerName
                ))
                .from(bannerDetail)
                .innerJoin(bannerDetail.banner, banner)
                .leftJoin(bannerDetailCategory).on(bannerDetailCategory.banner.id.eq(banner.id))
                .where(banner.id.eq(bannerId)
                        .and(banner.bannerType.in(bannerType))
                        .and(banner.isVisible.isTrue()))
                .fetchOne();
    }

    @Override
    public BannerDetailResponseDto.BannerDetailResponse findBannerOtherDetailByBannerId(BannerType bannerType, Long bannerId) {
        int defaultKeyNo = 0;

        return queryFactory
                .select(new QBannerDetailResponseDto_BannerDetailResponse(
                        banner.id,
                        bannerDetail.externalUrl,
                        bannerDetail.bannerDetailButtonName,
                        Expressions.asNumber(defaultKeyNo),
                        banner.bannerType,
                        banner.bannerName
                ))
                .from(bannerDetail)
                .innerJoin(bannerDetail.banner, banner)
                .leftJoin(bannerDetailOther).on(bannerDetailOther.banner.id.eq(banner.id))
                .where(banner.id.eq(bannerId)
                        .and(banner.bannerType.in(bannerType))
                        .and(banner.isVisible.isTrue()))
                .fetchOne();
    }

    @Override
    public List<BannerDetailResponseDto.BannerDetailResponse> findBannerDetailAll(BannerSectionType home) {
        return queryFactory
                .select(new QBannerDetailResponseDto_BannerDetailResponse(
                        banner.id,
                        bannerDetail.externalUrl,
                        bannerDetail.bannerDetailButtonName,
                        bannerDetail.bannerKeyNo,
                        banner.bannerType,
                        banner.bannerName
                ))
                .from(bannerDetail)
                .rightJoin(bannerDetail.banner, banner)
                .where(banner.isVisible.isTrue()
                        .and(banner.bannerSection.in(home)))
                .fetch();
    }

}

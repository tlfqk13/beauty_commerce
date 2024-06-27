package com.example.sampleroad.repository.banner;

import com.example.sampleroad.domain.banner.BannerSectionType;
import com.example.sampleroad.domain.banner.BannerType;
import com.example.sampleroad.dto.response.banner.BannerDetailResponseDto;

import java.util.List;

public interface BannerDetailRepositoryCustom {

    BannerDetailResponseDto.BannerDetailResponse findBannerCouponDetailByBannerId(BannerType bannerType, Long bannerId);
    BannerDetailResponseDto.BannerDetailResponse findBannerProductDetailByBannerId(BannerType bannerType, Long bannerId);
    BannerDetailResponseDto.BannerDetailResponse findBannerNoticeDetailByBannerId(BannerType bannerType, Long bannerId);
    BannerDetailResponseDto.BannerDetailResponse findBannerCategoryDetailByBannerId(BannerType bannerType, Long bannerId);
    BannerDetailResponseDto.BannerDetailResponse findBannerOtherDetailByBannerId(BannerType bannerType, Long bannerId);
    List<BannerDetailResponseDto.BannerDetailResponse> findBannerDetailAll(BannerSectionType home);
}

package com.example.sampleroad.repository.banner;

import com.example.sampleroad.domain.banner.BannerSectionType;
import com.example.sampleroad.dto.response.banner.BannerResponseDto;

import java.util.List;

public interface BannerRepositoryCustom {
    List<BannerResponseDto.BannerInfoQueryDto> findByBannerSection(boolean isVisible, List<BannerSectionType> bannerSectionType);
}

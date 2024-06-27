package com.example.sampleroad.dto.response.banner;

import com.example.sampleroad.domain.banner.BannerType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BannerDetailResponseDto<T> {
    @NoArgsConstructor
    @Getter
    public static class BannerDetailResponse {
        private BannerType bannerType;
        private Long bannerId;
        private String externalUrl;
        private List<String> bannerDetailImg;
        private String bannerDetailButtonName;
        private int bannerKeyNo;
        private String bannerName;

        @QueryProjection
        public BannerDetailResponse(Long bannerId, String externalUrl,
                                    String bannerDetailButtonName, int bannerKeyNo,
                                    BannerType bannerType, String bannerName) {
            this.bannerId = bannerId;
            this.externalUrl = externalUrl;
            this.bannerDetailButtonName = bannerDetailButtonName;
            this.bannerKeyNo = bannerKeyNo;
            this.bannerType = bannerType;
            this.bannerName = bannerName;
        }

        public BannerDetailResponse(BannerDetailResponse bannerDetailResponse, List<String> bannerDetailImg) {
            this.bannerType = bannerDetailResponse.getBannerType();
            this.bannerId = bannerDetailResponse.getBannerId();
            this.externalUrl = bannerDetailResponse.getExternalUrl();
            this.bannerDetailImg = bannerDetailImg;
            this.bannerDetailButtonName = bannerDetailResponse.getBannerDetailButtonName();
            this.bannerKeyNo = bannerDetailResponse.getBannerKeyNo();
            this.bannerName = bannerDetailResponse.getBannerName();
        }

        public BannerDetailResponse(BannerDetailResponse bannerDetailResponse, String bannerDetailButtonName, List<String> bannerDetailImg) {
            this.bannerType = bannerDetailResponse.getBannerType();
            this.bannerId = bannerDetailResponse.getBannerId();
            this.externalUrl = bannerDetailResponse.getExternalUrl();
            this.bannerDetailImg = bannerDetailImg;
            this.bannerDetailButtonName = bannerDetailButtonName;
            this.bannerKeyNo = bannerDetailResponse.getBannerKeyNo();
        }
    }
}

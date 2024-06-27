package com.example.sampleroad.dto.response.banner;

import com.example.sampleroad.domain.banner.BannerSectionType;
import com.example.sampleroad.domain.banner.BannerType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
public class BannerResponseDto {
    private double heightRatio;
    private double widthRatio;
    private List<BannerInfoDto> bannerList;

    public BannerResponseDto(double heightRatio, double widthRatio, List<BannerInfoDto> bannerList) {
        this.heightRatio = heightRatio;
        this.widthRatio = widthRatio;
        this.bannerList = bannerList;
    }

    @NoArgsConstructor
    @Getter
    public static class BannerInfoDto {
        private BannerSectionType bannerSectionType;
        private Long bannerId;
        private String bannerName;
        private String imageUrl;
        private int bannerKeyNo;
        private boolean isMoveBannerDetail;
        private BannerType bannerType;
        private String bannerKeyStr;

        public boolean getIsMoveBannerDetail() {
            return isMoveBannerDetail;
        }

        public BannerInfoDto(BannerSectionType bannerSectionType, Long bannerId, String bannerName, String imageUrl,
                             int bannerKeyNo, boolean isMoveBannerDetail, BannerType bannerType, String bannerKeyStr) {
            this.bannerSectionType = bannerSectionType;
            this.bannerId = bannerId;
            this.bannerName = bannerName;
            this.bannerKeyNo = bannerKeyNo;
            this.isMoveBannerDetail = isMoveBannerDetail;
            this.imageUrl = imageUrl;
            this.bannerType = bannerType;
            this.bannerKeyStr = bannerKeyStr;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class BannerInfoQueryDto {
        private BannerSectionType bannerSectionType;
        private Long bannerId;
        private String bannerName;
        private String imageUrl;
        private int bannerKeyNo;
        private boolean isMoveBannerDetail;
        private BannerType bannerType;
        private double heightRatio;
        private double widthRatio;
        private String bannerKeyStr;

        public boolean getIsMoveBannerDetail() {
            return isMoveBannerDetail;
        }

        @QueryProjection
        public BannerInfoQueryDto(BannerSectionType bannerSectionType, Long bannerId, String bannerName, String imageUrl,
                                  int bannerKeyNo, boolean isMoveBannerDetail, BannerType bannerType,
                                  double heightRatio, double widthRatio, String bannerKeyStr) {
            this.bannerSectionType = bannerSectionType;
            this.bannerId = bannerId;
            this.bannerName = bannerName;
            this.bannerKeyNo = bannerKeyNo;
            this.isMoveBannerDetail = isMoveBannerDetail;
            this.imageUrl = imageUrl;
            this.bannerType = bannerType;
            this.heightRatio = heightRatio;
            this.widthRatio = widthRatio;
            this.bannerKeyStr = bannerKeyStr;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class getBannerList {
        private BannerResponseDto homeBannerList;
        private BannerResponseDto homeMiddleBannerList;
        private BannerResponseDto homeSurveyBannerList;

        public getBannerList(BannerResponseDto homeBannerList, BannerResponseDto homeMiddleBannerList, BannerResponseDto homeSurveyBannerList) {
            this.homeBannerList = homeBannerList;
            this.homeMiddleBannerList = homeMiddleBannerList;
            this.homeSurveyBannerList = homeSurveyBannerList;
        }
    }
}

package com.example.sampleroad.dto.response.splash;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SplashResponseDto {

    private MainSplash mainSplash;
    private AdSplash adSplash;

    public SplashResponseDto(MainSplash mainSplash, AdSplash adSplash) {
        this.mainSplash = mainSplash;
        this.adSplash = adSplash;
    }

    @NoArgsConstructor
    @Getter
    public static class MainSplash {
        private String imgUrl;

        public MainSplash(String imgUrl) {
            this.imgUrl = imgUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class AdSplash {
        private Long splashId;
        private String imgUrl;
        private String endDate;

        public AdSplash(Long splashId, String imgUrl, String endDate) {
            this.splashId = splashId;
            this.imgUrl = imgUrl;
            this.endDate = endDate;
        }
    }
}

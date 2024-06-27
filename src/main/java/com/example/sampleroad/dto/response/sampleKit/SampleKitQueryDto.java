package com.example.sampleroad.dto.response.sampleKit;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SampleKitQueryDto {

    @NoArgsConstructor
    @Getter
    public static class SampleKit{
        private Long sampleKitId;
        private String sampleKitName;
        private int sampleKitProductNo;
        private String sampleKitImage;

        @QueryProjection
        public SampleKit(Long sampleKitId, String sampleKitName, int sampleKitProductNo, String sampleKitImage) {
            this.sampleKitId = sampleKitId;
            this.sampleKitName = sampleKitName;
            this.sampleKitProductNo = sampleKitProductNo;
            this.sampleKitImage = sampleKitImage;
        }
    }
}

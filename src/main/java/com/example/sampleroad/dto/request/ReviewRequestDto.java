package com.example.sampleroad.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewRequestDto {
    private int productNo; // 상품번호
    private Long optionNo;
    private Long orderOptionNo;
    private String content;
    private double rate;
    private String[] reviewTags;
    private String[] urls;
    @JsonIgnore
    private String extraJson = "string";
    private Boolean isSampleroadReview;

    public Boolean getIsSampleroadReview() {
        return isSampleroadReview;
    }

    @NoArgsConstructor
    @Getter
    public static class Create {

        private String tag;

        private String content;

        private int productNo;

        private String memberNo;
    }

    @NoArgsConstructor
    @Getter
    public static class ReadByMemberNo {
        private String memberNo;
    }

    @NoArgsConstructor
    @Getter
    public static class Update {

        private double rate;

        private String[] reviewTags;

        private String content;

        private String[] urls;

        public Update(double rate, String content, String[] urls) {
            this.rate = rate;
            this.content = content;
            this.urls = urls;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Delete {
        private int reviewNo;
        private int productNo;
    }

    @NoArgsConstructor
    @Getter
    public static class Report {
        private int reviewNo;
        private int productNo;
        private String content;
    }
}

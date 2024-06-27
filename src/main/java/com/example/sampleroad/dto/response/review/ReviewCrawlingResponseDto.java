package com.example.sampleroad.dto.response.review;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewCrawlingResponseDto {
    private String reviewLink;

    public ReviewCrawlingResponseDto(String reviewLink) {
        this.reviewLink = reviewLink;
    }
}

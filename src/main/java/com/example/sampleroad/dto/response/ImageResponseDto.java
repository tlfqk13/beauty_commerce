package com.example.sampleroad.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ImageResponseDto {

    private List<ImageUrl> imageUrls;

    public ImageResponseDto(List<ImageUrl> imageUrl) {
        this.imageUrls = imageUrl;
    }

    @NoArgsConstructor
    @Getter
    public static class ImageUrl {
        private String imageUrl;

        public ImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}

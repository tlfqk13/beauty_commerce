package com.example.sampleroad.dto.response.product;

import com.example.sampleroad.dto.response.customkit.CustomKitItemQueryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CustomKitItemResponseDto {

    @NoArgsConstructor
    @Getter
    public static class CustomKitItemInfo {
        List<CustomKitItemQueryDto> items;

        public CustomKitItemInfo(List<CustomKitItemQueryDto> items) {
            this.items = items;
        }
    }
}

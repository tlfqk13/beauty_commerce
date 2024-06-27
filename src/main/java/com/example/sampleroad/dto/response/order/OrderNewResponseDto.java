package com.example.sampleroad.dto.response.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class OrderNewResponseDto {

    private List<ItemGroup> itemGroup;

    public OrderNewResponseDto(List<ItemGroup> itemGroup) {
        this.itemGroup = itemGroup;
    }


    @NoArgsConstructor
    @Getter
    public static class ItemGroup {
        private boolean isCustomKit;
        private String sampleKitImage;
        private String sampleKitName;
        private String sampleKitBrandName;
        private int sampleKitOrderCnt;
        private int sampleKitPrice;
        private int sampleKitDiscountedPrice;
        private List<OrderCancelResponseDto.ProductList> sampleList;


        public boolean getIsCustomKit() {
            return isCustomKit;
        }

        public ItemGroup(boolean isCustomKit,
                         String sampleKitImage, String sampleKitName, String sampleKitBrandName,
                         int sampleKitOrderCnt, int sampleKitPrice, int sampleKitDiscountedPrice,
                         List<OrderCancelResponseDto.ProductList> sampleList) {
            this.isCustomKit = isCustomKit;
            this.sampleKitImage = sampleKitImage;
            this.sampleKitName = sampleKitName;
            this.sampleKitBrandName = sampleKitBrandName;
            this.sampleKitOrderCnt = sampleKitOrderCnt;
            this.sampleKitPrice = sampleKitPrice;
            this.sampleKitDiscountedPrice = sampleKitDiscountedPrice;
            this.sampleList = sampleList;
        }
    }
}

package com.example.sampleroad.dto.response.product;

import com.example.sampleroad.domain.CategoryType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class EventProductResponseDto {

    @NoArgsConstructor
    @Getter
    public static class EventProductList {
        List<EventProductInfo> eventProductInfoList;

        public EventProductList(List<EventProductInfo> eventProductInfoList) {
            this.eventProductInfoList = eventProductInfoList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class EventProductInfo {
        private CategoryType categoryType;
        private int productNo;
        private String productName;
        private String brandName;
        private String eventProductImgUrl;
        private String eventTitle;
        private String eventSubTitle;
        private String eventName;
        private int salePrice;
        private int eventPrice;
        private int stockCnt;
        private LocalDateTime localDateTime;

        public EventProductInfo(String eventTitle, String eventSubTitle, String eventName, String eventProductImgUrl,
                                int salePrice, int eventPrice, LocalDateTime localDateTime, int productNo,
                                int stockCnt, String productName, String brandName, CategoryType categoryType) {
            this.eventTitle = eventTitle;
            this.eventSubTitle = eventSubTitle;
            this.eventName = eventName;
            this.eventProductImgUrl = eventProductImgUrl;
            this.salePrice = salePrice;
            this.eventPrice = eventPrice;
            this.localDateTime = localDateTime;
            this.productNo = productNo;
            this.stockCnt = stockCnt;
            this.productName = productName;
            this.brandName = brandName;
            this.categoryType = categoryType;
        }
    }
}

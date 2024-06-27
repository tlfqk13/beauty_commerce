package com.example.sampleroad.dto.response.product;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.product.EventProductType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class EventProductQueryDto {

    @NoArgsConstructor
    @Getter
    public static class EventProductInfo{
        private String eventTitle;
        private String eventSubTitle;
        private String eventName;
        private String eventProductImgUrl;
        private int eventPrice;
        private LocalDateTime eventFinishTime;
        private int productNo;
        private String productName;
        private String brandName;
        private CategoryType categoryType;
        private EventProductType eventProductType;

        @QueryProjection
        public EventProductInfo(String eventTitle, String eventSubTitle, String eventName,
                                String eventProductImgUrl,
                                int eventPrice, LocalDateTime eventFinishTime, int productNo, String productName,
                                String brandName,CategoryType categoryType,
                                EventProductType eventProductType) {
            this.eventTitle = eventTitle;
            this.eventSubTitle = eventSubTitle;
            this.eventName = eventName;
            this.eventProductImgUrl = eventProductImgUrl;
            this.eventPrice = eventPrice;
            this.eventFinishTime = eventFinishTime;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.categoryType = categoryType;
            this.eventProductType = eventProductType;
        }
    }
}

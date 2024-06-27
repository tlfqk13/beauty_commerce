package com.example.sampleroad.dto.response.product;

import com.example.sampleroad.domain.CategoryType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductQueryDto {
    private int productNo;
    private int productOptionNo;
    private String productName;
    private String imgUrl;
    private Double reviewRating;
    private CategoryType categoryType;
    private Boolean isMultiPurchase;

    @QueryProjection
    public ProductQueryDto(int productNo, int productOptionNo,
                           String productName, String imgUrl, Double reviewRating,
                           CategoryType categoryType,
                           Boolean isMultiPurchase) {
        this.productNo = productNo;
        this.productOptionNo = productOptionNo;
        this.productName = productName;
        this.imgUrl = imgUrl;
        this.reviewRating = reviewRating;
        this.categoryType = categoryType;
        this.isMultiPurchase = isMultiPurchase;

    }

    @NoArgsConstructor
    @Getter
    public static class SearchProductQueryDto {
        private int productNo;

        @QueryProjection
        public SearchProductQueryDto(int productNo) {
            this.productNo = productNo;
        }
    }


}

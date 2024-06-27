package com.example.sampleroad.dto.response.product;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductInfoDto {
    private int productNo;
    private String productName;
    private String brandName;
    private String imageUrl;
    private int salePrice;
    private int immediateDiscountAmt;
    private int stockCnt;
    private Double reviewRating;
    private int totalReviewCount;
    private String displayCategoryNo;

    public ProductInfoDto(int productNo, String productName, String brandName,
                          String imageUrl, int salePrice, int immediateDiscountAmt,
                          int stockCnt, Double reviewRating, int totalReviewCount,
                          String displayCategoryNo) {
        this.productNo = productNo;
        this.productName = productName;
        this.brandName = brandName;
        this.imageUrl = imageUrl;
        this.salePrice = salePrice;
        this.immediateDiscountAmt = immediateDiscountAmt;
        this.stockCnt = stockCnt;
        this.reviewRating = reviewRating;
        this.totalReviewCount = totalReviewCount;
        this.displayCategoryNo = displayCategoryNo;
    }
}

package com.example.sampleroad.dto.response.product;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductReviewInfoDto {
    private int productNo;
    private int productOptionNo;
    private Double averageReviewRate;
    private Long reviewCount;

    @QueryProjection
    public ProductReviewInfoDto(int productNo, int productOptionNo, Double averageReviewRate, Long reviewCount) {
        this.productNo = productNo;
        this.productOptionNo = productOptionNo;
        this.averageReviewRate = averageReviewRate;
        this.reviewCount = reviewCount;
    }
}

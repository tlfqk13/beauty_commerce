package com.example.sampleroad.dto.response.product;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CustomKitQueryDto {
    private int productNo;
    private int productOptionNo;
    private String productName;
    private String brandName;
    private String imgUrl;
    private Double productReviewRate;

    @QueryProjection
    public CustomKitQueryDto(int productNo, int productOptionNo,
                             String productName, String brandName, String imgUrl,
                             Double productReviewRate) {
        this.productNo = productNo;
        this.productOptionNo = productOptionNo;
        this.productName = productName;
        this.brandName = brandName;
        this.imgUrl = imgUrl;
        this.productReviewRate = productReviewRate;
    }
}

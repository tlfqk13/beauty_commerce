package com.example.sampleroad.dto.response;

import com.example.sampleroad.domain.CategoryType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class BestSellerResponseQueryDto {

    private CategoryType productType;
    private String tag;
    private int productNo;
    private String productName;
    private String brandName;
    private String imageUrl;

    @QueryProjection
    public BestSellerResponseQueryDto(CategoryType productType, String tag, int productNo, String productName, String brandName, String imageUrl){
        this.productType = productType;
        this.tag = tag;
        this.productNo = productNo;
        this.productName = productName;
        this.brandName = brandName;
        this.imageUrl = imageUrl;
    }

    @NoArgsConstructor
    @Getter
    public static class BestSeller {
        private List<BestSellerResponseQueryDto> products;
        public BestSeller(List<BestSellerResponseQueryDto> products) {
            this.products = products;
        }
    }
}

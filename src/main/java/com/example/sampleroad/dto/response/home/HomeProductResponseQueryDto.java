package com.example.sampleroad.dto.response.home;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.product.HomeProductType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class HomeProductResponseQueryDto {
    private HomeProductType homeProductType;
    private Long productId;
    private int productNo;
    private String productName;
    private String brandName;
    private String tag;
    private String imgUrl;
    private CategoryType categoryType;
    private LocalDateTime localDateTime;
    @QueryProjection
    public HomeProductResponseQueryDto(HomeProductType homeProductType, Long productId, int productNo,
                                       String productName, String brandName,
                                       String tag, String imgUrl,
                                       CategoryType categoryType,
                                       LocalDateTime localDateTime) {
        this.homeProductType = homeProductType;
        this.productId = productId;
        this.productNo = productNo;
        this.productName = productName;
        this.brandName = brandName;
        this.tag = tag;
        this.imgUrl = imgUrl;
        this.categoryType = categoryType;
        this.localDateTime = localDateTime;
    }
}

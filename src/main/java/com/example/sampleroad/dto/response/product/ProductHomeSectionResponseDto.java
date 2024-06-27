package com.example.sampleroad.dto.response.product;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductHomeSectionResponseDto {
    private Long categoryId;
    private Long productId;
    private int productNo;

    @QueryProjection
    public ProductHomeSectionResponseDto(Long categoryId, Long productId, int productNo) {
        this.categoryId = categoryId;
        this.productId = productId;
        this.productNo = productNo;
    }
}

package com.example.sampleroad.dto.response.wishList;

import com.example.sampleroad.domain.CategoryType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class WishListQueryDto {

    private Long wishListId;
    private Long productId;
    private int productNo;
    private CategoryType categoryType;

    @QueryProjection
    public WishListQueryDto(Long wishListId, Long productId, int productNo,
                            CategoryType categoryType) {
        this.wishListId = wishListId;
        this.productId = productId;
        this.productNo = productNo;
        this.categoryType = categoryType;
    }
}

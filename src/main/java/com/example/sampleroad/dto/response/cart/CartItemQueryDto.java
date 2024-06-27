package com.example.sampleroad.dto.response.cart;

import com.example.sampleroad.domain.CategoryType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CartItemQueryDto {
    private Long cartId;
    private Long cartItemId;
    private int productNo;
    private int cartNo;
    private int productCount;
    private int productOptionNo;
    private CategoryType categoryDepth1;
    private boolean isCustomKit;

    @QueryProjection
    public CartItemQueryDto(Long cartId, Long cartItemId, int productNo, int cartNo, int productCount,
                            int productOptionNo, CategoryType categoryDepth1, boolean isCustomKit) {
        this.cartId = cartId;
        this.cartItemId = cartItemId;
        this.productNo = productNo;
        this.cartNo = cartNo;
        this.productCount = productCount;
        this.productOptionNo = productOptionNo;
        this.categoryDepth1 = categoryDepth1;
        this.isCustomKit = isCustomKit;
    }
}

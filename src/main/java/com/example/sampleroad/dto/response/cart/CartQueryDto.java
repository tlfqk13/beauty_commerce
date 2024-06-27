package com.example.sampleroad.dto.response.cart;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CartQueryDto {
    private Long cartId;
    private Long cartItemId;
    private String productName;
    private int productNo;
    private int orderCnt;
    private int productOptionNumber;
    private boolean isCustomKit;
    private Boolean isMultiPurchase;

    @QueryProjection
    public CartQueryDto(Long cartId, Long cartItemId, String productName, int productNo,
                        int orderCnt, int productOptionNumber, boolean isCustomKit,
                        Boolean isMultiPurchase) {
        this.cartId = cartId;
        this.cartItemId = cartItemId;
        this.productName = productName;
        this.productNo = productNo;
        this.orderCnt = orderCnt;
        this.productOptionNumber = productOptionNumber;
        this.isCustomKit = isCustomKit;
        this.isMultiPurchase = isMultiPurchase;
    }
}

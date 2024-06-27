package com.example.sampleroad.dto.response.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class CartProductResponseDto {

    @NoArgsConstructor
    @Getter
    @Setter
    public static class CartProductInfo {
        private Long cartId;
        private int productNo;
        private int productOptionNo;
        private int stockCnt;
        private int orderCnt;
        private String productName;
        private String brandName;
        private String imageUrl;
        private String displayCategoryNo;
        private int salePrice;
        private int immediateDiscountAmt;

        public CartProductInfo(Long cartId, int productNo, int productOptionNo,
                               int stockCnt, int orderCnt, String productName,
                               String brandName, String imageUrl, String displayCategoryNo,
                               int salePrice, int immediateDiscountAmt) {
            this.cartId = cartId;
            this.productNo = productNo;
            this.productOptionNo = productOptionNo;
            this.stockCnt = stockCnt;
            this.orderCnt = orderCnt;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
            this.displayCategoryNo = displayCategoryNo;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
        }
    }
}

package com.example.sampleroad.dto.response.display;

import com.example.sampleroad.dto.response.cart.ICartProductInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class DisplaySectionResponseDto  {

    @NoArgsConstructor
    @Getter
    public static class SectionItem implements ICartProductInfo {
        private int displayNo;
        private int productNo;
        private String productName;
        private String brandName;
        private String imageUrl;
        private String displayCategoryNo;
        private int salePrice;
        private int immediateDiscountAmt;
        private LocalDateTime saleStartYmdt;
        private LocalDateTime saleEndYmdt;

        public SectionItem(int productNo, String productName, String brandName,
                           String imageUrl, String displayCategoryNo, int salePrice, int immediateDiscountAmt,
                           LocalDateTime saleStartYmdt, LocalDateTime saleEndYmdt) {
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
            this.displayCategoryNo = displayCategoryNo;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.saleStartYmdt = saleStartYmdt;
            this.saleEndYmdt = saleEndYmdt;
        }

        public SectionItem(int productNo, String productName, String brandName,
                           String imageUrl, int salePrice, int immediateDiscountAmt,
                           LocalDateTime saleStartYmdt, LocalDateTime saleEndYmdt) {
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.saleStartYmdt = saleStartYmdt;
            this.saleEndYmdt = saleEndYmdt;
        }

        public SectionItem(int displayNo) {
            this.displayNo = displayNo;
        }

        @Override
        public int getStockCnt() {
            return 0;
        }
    }
}

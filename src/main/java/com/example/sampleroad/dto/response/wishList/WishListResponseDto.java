package com.example.sampleroad.dto.response.wishList;

import com.example.sampleroad.domain.WishList;
import com.example.sampleroad.domain.product.ProductType;
import com.example.sampleroad.dto.response.cart.ICartProductInfo;
import com.example.sampleroad.dto.response.product.ProductInfoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class WishListResponseDto {
    List<AllWishList> allWishList = new ArrayList<>();

    public WishListResponseDto(List<AllWishList> wishList) {
        this.allWishList = wishList;
    }

    @NoArgsConstructor
    @Getter
    public static class AllWishList {
        private int productNo;
        private LocalDateTime createdAt;

        public AllWishList(WishList wishList) {
            this.productNo = wishList.getProduct().getProductNo();
            this.createdAt = wishList.getCreatedAt();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class WishListProducts implements ICartProductInfo {
        private ProductType productType;
        private int productNo;
        private int stockCnt;
        private String productName;
        private String displayCategoryNo;
        private int salePrice;
        private int immediateDiscountAmt;
        private Double reviewRating;
        private int totalReviewCount;
        private String brandName;
        private String imageUrl;

        public WishListProducts(ProductType productType,
                                ProductInfoDto productInfo) {
            this.productType = productType;
            this.productNo = productInfo.getProductNo();
            this.stockCnt = productInfo.getStockCnt();
            this.productName = productInfo.getProductName();
            this.displayCategoryNo = productInfo.getDisplayCategoryNo();
            this.salePrice = productInfo.getSalePrice();
            this.immediateDiscountAmt = productInfo.getImmediateDiscountAmt();
            this.reviewRating = productInfo.getReviewRating();
            this.totalReviewCount = productInfo.getTotalReviewCount();
            this.brandName = productInfo.getBrandName();
            this.imageUrl = productInfo.getImageUrl();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class AllWishListFromShopby {
        private int count;
        private List<WishListResponseDto.WishListProducts> products;

        public AllWishListFromShopby(int count, List<WishListResponseDto.WishListProducts> products) {
            this.count = count;
            this.products = products;
        }
    }
}

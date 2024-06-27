package com.example.sampleroad.dto.response.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
public class CustomKitResponseDto {

    private Long totalCount;
    private List<CustomKitItemInfo> item;

    public CustomKitResponseDto(Long totalCount, List<CustomKitItemInfo> item) {
        this.totalCount = totalCount;
        this.item = item;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class CustomKitItemInfo implements IProductInfoBase {
        private int categoryNo;
        private int productNo;
        private String productName;
        private String brandName;
        private String imgUrl;
        private Double viewRating;
        private int totalReviewCount;
        private int salePrice;
        private int immediateDiscountAmt;
        private int stockCnt;
        private Boolean isWishList;

        public Boolean getIsWishList() {
            return isWishList;
        }


        public void setIsWishList(Boolean wishList) {
            isWishList = wishList;
        }

        public CustomKitItemInfo(int categoryNo, int productNo, String productName, String brandName, String imgUrl,
                                 Double viewRating, int totalReviewCount, int salePrice, int immediateDiscountAmt,
                                 int stockCnts) {
            this.categoryNo = categoryNo;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imgUrl = imgUrl;
            this.viewRating = viewRating;
            this.totalReviewCount = totalReviewCount;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.stockCnt = stockCnts;
        }

        public CustomKitItemInfo(int categoryNo, int productNo, String productName, String brandName, String imgUrl,
                                 Double viewRating, int totalReviewCount, int salePrice, int immediateDiscountAmt,
                                 int stockCnts, boolean isWishList) {
            this.categoryNo = categoryNo;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imgUrl = imgUrl;
            this.viewRating = viewRating;
            this.totalReviewCount = totalReviewCount;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.stockCnt = stockCnts;
            this.isWishList = isWishList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CustomKitItemInfos {
        List<CustomKitItemInfo> customKitItemInfos;

        public CustomKitItemInfos(List<CustomKitItemInfo> customKitItemInfos) {
            this.customKitItemInfos = customKitItemInfos;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class CustomKitCartItemInfo {
        private int productNo;
        private String productName;
        private String brandName;
        private String imgUrl;
        private Double viewRating;
        private int totalReviewCount;
        private int salePrice;
        private int immediateDiscountAmt;
        private int stockCnt;
        private int productOptionNo;

        public CustomKitCartItemInfo(int productNo, String productName, String brandName, String imgUrl,
                                     Double viewRating, int totalReviewCount, int salePrice, int immediateDiscountAmt,
                                     int stockCnt, int productOptionNo) {
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imgUrl = imgUrl;
            this.viewRating = viewRating;
            this.totalReviewCount = totalReviewCount;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.stockCnt = stockCnt;
            this.productOptionNo = productOptionNo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CustomKitCartItemInfos {
        List<CustomKitCartItemInfo> customKitCartItemInfo;

        public CustomKitCartItemInfos(List<CustomKitCartItemInfo> customKitCartItemInfo) {
            this.customKitCartItemInfo = customKitCartItemInfo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CustomKitFromCategory {
        private boolean hasCart;
        private Long totalCount;
        private List<CustomKitItemInfo> item;

        public CustomKitFromCategory(boolean hasCart, Long totalCount, List<CustomKitItemInfo> item) {
            this.hasCart = hasCart;
            this.totalCount = totalCount;
            this.item = item;
        }
    }
}


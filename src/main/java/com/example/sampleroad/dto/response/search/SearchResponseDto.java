package com.example.sampleroad.dto.response.search;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.dto.response.BestSellerResponseDto;
import com.example.sampleroad.dto.response.home.HomeResponseDto;
import com.example.sampleroad.dto.response.product.IProductInfoBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
public class SearchResponseDto {
    private SearchResponseDto.SearchResult searchResult;

    public SearchResponseDto(SearchResponseDto.SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    @NoArgsConstructor
    @Getter
    public static class SearchMain {
        private List<String> favoriteKeywordList;
        private HomeResponseDto.HomeCategoryList homeCategoryList;
        private SearchResponseDto.BestSeller bestSeller;

        public SearchMain(List<String> favoriteKeywordList, HomeResponseDto.HomeCategoryList homeCategoryList,
                          BestSeller bestSeller) {
            this.favoriteKeywordList = favoriteKeywordList;
            this.homeCategoryList = homeCategoryList;
            this.bestSeller = bestSeller;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RecentlyProduct {
        private int productNo;
        private String productName;
        private String brandName;
        private String imageUrl;
        private int salePrice;
        private int immediateDiscountAmt;

        public RecentlyProduct(int productNo, String productName, String brandName, String imageUrl,
                               int salePrice, int immediateDiscountAmt) {
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class SearchResult {
        private Long totalCount;
        private List<SearchResponseDto.SearchResultItemInfo> item;

        public SearchResult(Long totalCount, List<SearchResultItemInfo> item) {
            this.totalCount = totalCount;
            this.item = item;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class BestSeller {
        private int categoryNo;
        private List<SearchBestSellerResponseDto> products;

        public BestSeller(int categoryNo, List<SearchBestSellerResponseDto> products) {
            this.categoryNo = categoryNo;
            this.products = products;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class SearchBestSellerResponseDto implements IProductInfoBase {

        private CategoryType productType;
        private Boolean isWishList;
        private int productNo;
        private String productName;
        private String brandName;
        private String imageUrl;
        private int salePrice;
        private int immediateDiscountAmt;
        private String imgUrl;
        private int stockCnt;

        public SearchBestSellerResponseDto(Boolean isWishList, BestSellerResponseDto bestSeller) {
            this.productType = bestSeller.getProductType();
            this.isWishList = isWishList;
            this.productNo = bestSeller.getProductNo();
            this.productName = bestSeller.getProductName();
            this.brandName = bestSeller.getBrandName();
            this.imageUrl = bestSeller.getImageUrl();
            this.salePrice = bestSeller.getSalePrice();
            this.immediateDiscountAmt = bestSeller.getImmediateDiscountAmt();
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class SearchResultItemInfo {
        private CategoryType productType;
        private Boolean isWishList;
        private int productNo;
        private String displayCategoryNo;
        private String productName;
        private String brandName;
        private String imgUrl;
        private int salePrice;
        private int immediateDiscountAmt;
        private int stockCnt;

        public SearchResultItemInfo(CategoryType productType, Boolean isWishList, int productNo,
                                    String productName, String brandName, String imgUrl,
                                    int salePrice, int immediateDiscountAmt, int stockCnt) {
            this.productType = productType;
            this.isWishList = isWishList;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imgUrl = imgUrl;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.stockCnt = stockCnt;
        }

        public SearchResultItemInfo(CategoryType categoryType, int productNo, String productName, String brandName,
                                    String imgUrl, int salePrice,
                                    int immediateDiscountAmt, int stockCnt,
                                    String displayCategoryNo) {
            this.productType = categoryType;
            this.isWishList = false;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imgUrl = imgUrl;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.stockCnt = stockCnt;
            this.displayCategoryNo = displayCategoryNo;
        }

        public SearchResultItemInfo(CategoryType productType, int productNo, String displayCategoryNo,
                                    String productName, String brandName, String imgUrl,
                                    int salePrice, int immediateDiscountAmt, int stockCnt) {
            this.productType = productType;
            this.productNo = productNo;
            this.displayCategoryNo = displayCategoryNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imgUrl = imgUrl;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.stockCnt = stockCnt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class SearchBrandItemInfo extends SearchResultItemInfo {
        public SearchBrandItemInfo(SearchResponseDto.SearchResultItemInfo dto) {
            super(dto.getProductType(), dto.getIsWishList(), dto.getProductNo(), dto.getProductName(),
                    dto.getBrandName(), dto.getImgUrl(), dto.getSalePrice(),
                    dto.getImmediateDiscountAmt(), dto.getStockCnt());
        }
    }
}

package com.example.sampleroad.dto.response.home;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.home.HomeSectionType;
import com.example.sampleroad.domain.home.MoveCase;
import com.example.sampleroad.domain.search.SearchSortType;
import com.example.sampleroad.dto.response.banner.BannerResponseDto;
import com.example.sampleroad.dto.response.product.ProductInfoDto;
import com.example.sampleroad.dto.response.push.NotificationResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
public class HomeResponseDto {
    private BannerResponseDto bannerList;
    private Boolean isFirstPurchase;
    private HomeResponseDto.PopUpList popUpSection;
    private HomeResponseDto.HomeCategoryList homeCategoryList;
    private List<HomeResponseDto.ProductSectionInfos> sections;
    private NotificationResponseDto notification;
    private String instagramUrl;
    private String blogUrl;
    private String facebookUrl;
    private boolean hasCart;
    private boolean hasZeroExperience;

    public HomeResponseDto(BannerResponseDto bannerList,
                           Boolean isFirstPurchase,
                           HomeResponseDto.PopUpList popUpSection,
                           HomeResponseDto.HomeCategoryList homeCategoryList,
                           List<HomeResponseDto.ProductSectionInfos> sections,
                           NotificationResponseDto notification,
                           String instagramUrl, String blogUrl, String facebookUrl,
                           boolean hasCart, boolean hasZeroExperience) {
        this.bannerList = bannerList;
        this.isFirstPurchase = isFirstPurchase;
        this.popUpSection = popUpSection;
        this.homeCategoryList = homeCategoryList;
        this.sections = sections;
        this.notification = notification;
        this.instagramUrl = instagramUrl;
        this.blogUrl = blogUrl;
        this.facebookUrl = facebookUrl;
        this.hasCart = hasCart;
        this.hasZeroExperience = hasZeroExperience;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ProductSectionDto {
        private CategoryType categoryType;
        private int productNo;
        private String productName;
        private String brandName;
        private String imageUrl;
        private Double viewRating;
        private Integer totalReviewCount;
        private Integer salePrice;
        private Integer immediateDiscountAmt;
        private Integer stockCnt;
        private String startDate;
        private String endDate;
        private String displayCategoryNo;
        private List<String> subImageUrls;
        private Boolean isWishList;

        public Boolean getIsWishList() {
            return isWishList;
        }

        public void setIsWishList(Boolean wishList) {
            isWishList = wishList;
        }

        public ProductSectionDto(CategoryType categoryType, int productNo, String productName,
                                 String brandName, String imageUrl,
                                 Double viewRating, Integer totalReviewCount, Integer salePrice, Integer immediateDiscountAmt,
                                 Integer stockCnts) {
            this.categoryType = categoryType;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
            this.viewRating = viewRating;
            this.totalReviewCount = totalReviewCount;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.stockCnt = stockCnts;
        }

        public ProductSectionDto(CategoryType categoryType, int productNo, String productName,
                                 String brandName, int salePrice, int immediateDiscountAmt,
                                 String imageUrl, String startDate, String endDate) {
            this.categoryType = categoryType;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.imageUrl = imageUrl;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public ProductSectionDto(CategoryType categoryType, int productNo, String productName,
                                 String brandName, String imageUrl, String startDate, String endDate) {
            this.categoryType = categoryType;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public ProductSectionDto(int productNo, String imageUrl) {
            this.productNo = productNo;
            this.imageUrl = imageUrl;
        }

        public ProductSectionDto(CategoryType categoryType, ProductInfoDto productInfo) {
            this.categoryType = categoryType;
            this.productNo = productInfo.getProductNo();
            this.productName = productInfo.getProductName();
            this.brandName = productInfo.getBrandName();
            this.imageUrl = productInfo.getImageUrl();
            this.viewRating = productInfo.getReviewRating();
            this.totalReviewCount = productInfo.getTotalReviewCount();
            this.salePrice = productInfo.getSalePrice();
            this.immediateDiscountAmt = productInfo.getImmediateDiscountAmt();
            this.stockCnt = productInfo.getStockCnt();
            this.displayCategoryNo = productInfo.getDisplayCategoryNo();
        }

        public ProductSectionDto(CategoryType categoryType, int productNo,
                                 String productName, String brandName,
                                 int salePrice, int immediateDiscountAmt,
                                 String productImgUrl, int stockCnt,
                                 List<String> subImageUrls) {
            this.categoryType = categoryType;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.imageUrl = productImgUrl;
            this.stockCnt = stockCnt;
            this.subImageUrls = subImageUrls;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ProductSectionInfos {
        private HomeSectionType sectionCase;
        private String sectionTitle;
        private String sectionSubTitle;
        private MoveCase moveCase;
        private Integer moveKeyNumber; // ==categoryNumber
        private String moveKeyStr;
        List<HomeResponseDto.ProductSectionDto> products;

        public ProductSectionInfos(HomeSectionType sectionCase, MoveCase moveCase, Integer moveKeyNumber,
                                   String sectionTitle, String sectionSubTitle,
                                   List<HomeResponseDto.ProductSectionDto> products,
                                   String moveKeyStr) {
            this.sectionCase = sectionCase;
            this.moveCase = moveCase;
            this.moveKeyNumber = moveKeyNumber;
            this.sectionTitle = sectionTitle;
            this.sectionSubTitle = sectionSubTitle;
            this.products = products;
            this.moveKeyStr = moveKeyStr;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class CategoryInfo {
        private int categoryNo;
        private String categoryName;
        private String categoryIcon;
        private String badgeColorCode;
        private String badgeTitle;
        private SearchSortType sortType;

        public CategoryInfo(int categoryNo, String categoryName, String categoryIcon, String badgeTitle,
                            String badgeColorCode, SearchSortType sortType) {
            this.categoryNo = categoryNo;
            this.categoryName = categoryName;
            this.categoryIcon = categoryIcon;
            this.badgeTitle = badgeTitle;
            this.badgeColorCode = badgeColorCode;
            this.sortType = sortType;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class HomeCategoryList {
        private List<CategoryInfo> CategoryInfo;

        public HomeCategoryList(List<CategoryInfo> CategoryInfo) {
            this.CategoryInfo = CategoryInfo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class PopUpList {
        private Boolean isVisibleToday;
        private List<PopUpResponseDto.HomePopUpInfo> popUpInfoList;

        public PopUpList(Boolean isVisibleToday, List<PopUpResponseDto.HomePopUpInfo> popUpInfoList) {
            this.isVisibleToday = isVisibleToday;
            this.popUpInfoList = popUpInfoList;
        }
    }

}

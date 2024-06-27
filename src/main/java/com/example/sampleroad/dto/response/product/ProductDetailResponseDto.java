package com.example.sampleroad.dto.response.product;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.domain.product.ProductType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProductDetailResponseDto {
    boolean hasProductCoupon;
    private int productNo;
    private String productName;
    private String brandName;
    private int brandNo;
    private String[] imageUrls;
    private int stock;
    private int maxCnt;
    private int salePrice;
    private int immediateDiscountAmt;
    private int deliveryFee;
    private int returnDeliveryAmt;
    private int likeCnt;
    private Long reviewCnt;
    private Double reviewRating;
    private int[] optionNo;
    private String[] labels;
    private String[] values;
    private Integer[] addPrices;
    private Integer[] stockCnts;
    private int categoryNo;
    private String[] contentImageUrl;
    private List<String> dutyInfos;

    public ProductDetailResponseDto(ProductDetailResponseDto productDetailResponseDto,
                                    int[] optionNo, String[] labels, String[] values,
                                    Integer[] addPrices, Integer[] stockCnts) {
        this.hasProductCoupon = productDetailResponseDto.isHasProductCoupon();
        this.productNo = productDetailResponseDto.getProductNo();
        this.productName = productDetailResponseDto.getProductName();
        this.brandName = productDetailResponseDto.getBrandName();
        this.brandNo = productDetailResponseDto.getBrandNo();
        this.imageUrls = productDetailResponseDto.getImageUrls();
        this.stock = productDetailResponseDto.getStock();
        this.salePrice = productDetailResponseDto.getSalePrice();
        this.immediateDiscountAmt = productDetailResponseDto.getImmediateDiscountAmt();
        this.deliveryFee = productDetailResponseDto.getDeliveryFee();
        this.returnDeliveryAmt = productDetailResponseDto.getReturnDeliveryAmt();
        this.likeCnt = productDetailResponseDto.getLikeCnt();
        this.reviewCnt = productDetailResponseDto.getReviewCnt();
        this.reviewRating = productDetailResponseDto.getReviewRating();
        this.optionNo = optionNo;
        this.labels = labels;
        this.values = values;
        this.addPrices = addPrices;
        this.stockCnts = stockCnts;
        this.contentImageUrl = productDetailResponseDto.getContentImageUrl();
        this.dutyInfos = productDetailResponseDto.getDutyInfos();
    }

    public ProductDetailResponseDto(boolean hasProductCoupon, int productNo, String productName,
                                    String brandName, int brandNo, String[] imageUrls,
                                    int stock, int salePrice,
                                    int immediateDiscountAmt, int likeCnt,
                                    int deliveryFee, int returnDeliveryAmt,
                                    String[] contentImageUrl, List<String> dutyInfos,
                                    Long reviewCnt,Double reviewRating) {
        this.hasProductCoupon = hasProductCoupon;
        this.productNo = productNo;
        this.productName = productName;
        this.brandName = brandName;
        this.brandNo = brandNo;
        this.imageUrls = imageUrls;
        this.stock = stock;
        this.salePrice = salePrice;
        this.immediateDiscountAmt = immediateDiscountAmt;
        this.likeCnt = likeCnt;
        this.deliveryFee = deliveryFee;
        this.returnDeliveryAmt = returnDeliveryAmt;
        this.contentImageUrl = contentImageUrl;
        this.dutyInfos = dutyInfos;
        this.reviewCnt = reviewCnt;
        this.reviewRating = reviewRating;
    }

    @NoArgsConstructor
    @Getter
    public static class SampleList {
        private int productNo;
        private String productName;
        private String brandName;
        private String imageUrl;
        private int sampleKitProductNo;
        private int categoryTypeDepth1Number;

        public SampleList(Product product) {
            this.productNo = product.getProductNo();
            this.productName = product.getProductName();
            this.brandName = product.getBrandName();
            this.imageUrl = product.getImgUrl();
        }

        @QueryProjection
        public SampleList(int productNo, String productName, String brandName, String imageUrl, int categoryTypeDepth1Number) {
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
            this.categoryTypeDepth1Number = categoryTypeDepth1Number;
        }

        @QueryProjection
        public SampleList(int productNo, String productName, String brandName, String imageUrl
                , int sampleKitProductNo, int categoryTypeDepth1Number) {
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
            this.sampleKitProductNo = sampleKitProductNo;
            this.categoryTypeDepth1Number = categoryTypeDepth1Number;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ProductInfo {
        private boolean hasCart;
        private boolean isWishList;
        private boolean isMultiPurchase;
        private boolean isRestockNotification;
        private String deliveryInfo;
        private String deliveryDate;
        private ProductType productType;
        private List<SampleList> sampleList;
        private ProductDetailResponseDto productInfo;
        private Double recommendPercentage;
        private String relatedSectionName;
        private ProductDetailResponseDto.GroupPurchaseSection groupPurchaseSection;


        public ProductInfo(boolean hasCart, boolean isWishList,
                           String deliveryInfo, String deliveryDate,
                           ProductType productType, List<SampleList> sampleList,
                           ProductDetailResponseDto productInfo, Double recommendPercentage,
                           String relatedSectionName) {
            this.hasCart = hasCart;
            this.isWishList = isWishList;
            this.deliveryInfo = deliveryInfo;
            this.deliveryDate = deliveryDate;
            this.productType = productType;
            this.sampleList = sampleList;
            this.productInfo = productInfo;
            this.recommendPercentage = recommendPercentage;
            this.relatedSectionName = relatedSectionName;
        }

        public ProductInfo(boolean hasCart, boolean isWishList, boolean isMultiPurchase,
                           boolean isRestockNotification,
                           String deliveryInfo, String deliveryDate,
                           ProductType productType, List<SampleList> sampleList,
                           ProductDetailResponseDto productInfo, Double recommendPercentage,
                           String relatedSectionName,
                           ProductDetailResponseDto.GroupPurchaseSection groupPurchaseSection) {
            this.hasCart = hasCart;
            this.isWishList = isWishList;
            this.isMultiPurchase = isMultiPurchase;
            this.isRestockNotification = isRestockNotification;
            this.deliveryInfo = deliveryInfo;
            this.deliveryDate = deliveryDate;
            this.productType = productType;
            this.sampleList = sampleList;
            this.productInfo = productInfo;
            this.recommendPercentage = recommendPercentage;
            this.relatedSectionName = relatedSectionName;
            this.groupPurchaseSection = groupPurchaseSection;
        }

        public boolean getIsWishList() {
            return isWishList;
        }

        public boolean getIsMultiPurchase() {
            return isMultiPurchase;
        }

        public boolean getIsRestockNotification() {
            return isRestockNotification;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GroupPurchaseSection {
        private int roomCapacity;
        private int maxCnt;
        private List<ProductDetailResponseDto.GroupPurchaseRoom> groupPurchaseRoomList;

        public GroupPurchaseSection(int roomCapacity, int maxCnt, List<ProductDetailResponseDto.GroupPurchaseRoom> groupPurchaseRoomList) {
            this.roomCapacity = roomCapacity;
            this.maxCnt = maxCnt;
            this.groupPurchaseRoomList = groupPurchaseRoomList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GroupPurchaseRoom {
        private Long roomId;
        private String hostProfileImageUrl;
        private String hostNickName;
        private int remainMemberCnt;
        private String deadLine;
        private boolean isTeamMember;

        public boolean getIsTeamMember() {
            return isTeamMember;
        }

        public GroupPurchaseRoom(Long roomId,
                                 String hostProfileImageUrl, String hostNickName,
                                 int remainMemberCnt, String deadLine,
                                 boolean isTeamMember) {
            this.roomId = roomId;
            this.hostProfileImageUrl = hostProfileImageUrl;
            this.hostNickName = hostNickName;
            this.remainMemberCnt = remainMemberCnt;
            this.deadLine = deadLine;
            this.isTeamMember = isTeamMember;
        }
    }


    @NoArgsConstructor
    @Getter
    public static class RecentProducts {
        private int totalCount;
        private List<RecentProductInfo> recentProductInfoList;
        private int emptyMoveCategoryNo;

        public RecentProducts(int totalCount, List<RecentProductInfo> recentProductInfoList, int emptyMoveCategoryNo) {
            this.totalCount = totalCount;
            this.recentProductInfoList = recentProductInfoList;
            this.emptyMoveCategoryNo = emptyMoveCategoryNo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RecentProductInfo {
        private CategoryType productType;
        private Boolean isWishList;
        private int productNo;
        private String productName;
        private String brandName;
        private String imgUrl;
        private int salePrice;
        private int immediateDiscountAmt;
        private int stockCnt;

        public RecentProductInfo(CategoryType categoryType, int productNo, String productName, String brandName,
                                 String imgUrl, int salePrice, int immediateDiscountAmt, int stockCnt) {
            this.productType = categoryType;
            this.isWishList = false;
            this.productNo = productNo;
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
    public static class RecommendProductResponseDto {
        private int productNo;
        private String productName;
        private String productImageUrl;

        public RecommendProductResponseDto(int productNo, String productName, String productImageUrl) {
            this.productNo = productNo;
            this.productName = productName;
            this.productImageUrl = productImageUrl;
        }
    }
}

package com.example.sampleroad.dto.response.product;

import com.example.sampleroad.domain.product.ProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class NewProductResponseDto {

    @NoArgsConstructor
    @Getter
    public static class ProductSectionInfos {
        private boolean hasCart;
        private boolean isWishList;
        private ProductType productType;
        private List<String> productMainImageSection;
        private List<String> productDetailImageSection;
        private NewProductResponseDto.ProductInfoSection productInfoSection;
        private GroupPurchaseSection groupPurchaseSection;
        private RelatedProductSection relatedProductSection;

        public ProductSectionInfos(boolean hasCart, boolean isWishList,
                                   ProductType productType,
                                   List<String> productMainImageSection,
                                   List<String> productDetailImageSection,
                                   NewProductResponseDto.ProductInfoSection productInfoSection,
                                   GroupPurchaseSection groupPurchaseSection, RelatedProductSection relatedProductSection) {
            this.hasCart = hasCart;
            this.isWishList = isWishList;
            this.productType = productType;
            this.productMainImageSection = productMainImageSection;
            this.productDetailImageSection = productDetailImageSection;
            this.productInfoSection = productInfoSection;
            this.groupPurchaseSection = groupPurchaseSection;
            this.relatedProductSection = relatedProductSection;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ProductInfoSection {
        // TODO: 2/9/24
        private String deliveryInfo;
        private String deliveryDate;
        private Double recommendPercentage;
        boolean hasProductCoupon;
        private int productNo;
        private String productName;
        private String brandName;
        private int stock;
        private int salePrice;
        private int immediateDiscountAmt;
        private int deliveryFee;
        private int returnDeliveryAmt;
        private int likeCnt;
        private long reviewCnt;
        private Double reviewRating;
        private int[] optionNo;
        private String[] labels;
        private String[] values;
        private Integer[] addPrices;
        private Integer[] stockCnts;
        private int categoryNo;

        public ProductInfoSection(String deliveryInfo, String deliveryDate,
                                  Double recommendPercentage,
                                  boolean hasProductCoupon, int productNo, String productName,
                                  String brandName, int stock,
                                  int salePrice, int immediateDiscountAmt,
                                  int deliveryFee, int returnDeliveryAmt,
                                  int likeCnt, long reviewCnt,
                                  Double reviewRating, int[] optionNo,
                                  String[] labels, String[] values,
                                  Integer[] addPrices, Integer[] stockCnts,
                                  int categoryNo) {
            this.deliveryInfo = deliveryInfo;
            this.deliveryDate = deliveryDate;
            this.recommendPercentage = recommendPercentage;
            this.hasProductCoupon = hasProductCoupon;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.stock = stock;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.deliveryFee = deliveryFee;
            this.returnDeliveryAmt = returnDeliveryAmt;
            this.likeCnt = likeCnt;
            this.reviewCnt = reviewCnt;
            this.reviewRating = reviewRating;
            this.optionNo = optionNo;
            this.labels = labels;
            this.values = values;
            this.addPrices = addPrices;
            this.stockCnts = stockCnts;
            this.categoryNo = categoryNo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ProductCouponSection {

    }

    @NoArgsConstructor
    @Getter
    public static class GroupPurchaseSection {
        private List<NewProductResponseDto.GroupPurchaseRoom> groupPurchaseRoomList;

        public GroupPurchaseSection(List<NewProductResponseDto.GroupPurchaseRoom> groupPurchaseRoomList) {
            this.groupPurchaseRoomList = groupPurchaseRoomList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GroupPurchaseRoom {
        private Long roomId;
        private LocalDateTime deadLine;
        private boolean isFull;
        private int roomCapacity;
        private int productNo;

        public GroupPurchaseRoom(Long roomId, LocalDateTime deadLine, boolean isFull, int roomCapacity, int productNo) {
            this.roomId = roomId;
            this.deadLine = deadLine;
            this.isFull = isFull;
            this.roomCapacity = roomCapacity;
            this.productNo = productNo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class RelatedProductSection {
        private String relatedSectionName;
        private List<ProductDetailResponseDto.SampleList> relateProductList;

        public RelatedProductSection(String relatedSectionName,
                                     List<ProductDetailResponseDto.SampleList> relateProductList) {
            this.relatedSectionName = relatedSectionName;
            this.relateProductList = relateProductList;
        }
    }

}

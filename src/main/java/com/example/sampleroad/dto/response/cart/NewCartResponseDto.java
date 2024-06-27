package com.example.sampleroad.dto.response.cart;

import com.example.sampleroad.domain.home.MoveCase;
import com.example.sampleroad.domain.product.ProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class NewCartResponseDto {
    private int baseDeliveryAmt;// 기본 배송비
    private int aboveDeliveryAmt;// 기준 배송비
    private List<NewCartResponseDto.CartSectionResponseDto> sections;

    public NewCartResponseDto(int baseDeliveryAmt, int aboveDeliveryAmt, List<CartSectionResponseDto> sections) {
        this.baseDeliveryAmt = baseDeliveryAmt;
        this.aboveDeliveryAmt = aboveDeliveryAmt;
        this.sections = sections;
    }

    @NoArgsConstructor
    @Getter
    public static class CartSectionResponseDto {
        private String sectionCase;
        private String sectionTitle;
        private String sectionSubTitle;
        private Integer maxCartCount;
        private MoveCase moveCase;
        private List<ProductType> productTypeList;
        private List<?> products;

        public CartSectionResponseDto(String sectionCase, String sectionTitle,
                                      String sectionSubTitle, Integer maxCartCount,
                                      List<InCartProduct> products) {
            this.sectionCase = sectionCase;
            this.sectionTitle = sectionTitle;
            this.sectionSubTitle = sectionSubTitle;
            this.maxCartCount = maxCartCount;
            this.products = products;
        }

        public CartSectionResponseDto(String sectionCase, String sectionTitle,
                                      List<?> products) {
            this.sectionCase = sectionCase;
            this.sectionTitle = sectionTitle;
            this.products = products;
        }

        public CartSectionResponseDto(String sectionCase, String sectionTitle,
                                      List<?> products,MoveCase moveCase) {
            this.sectionCase = sectionCase;
            this.sectionTitle = sectionTitle;
            this.moveCase = moveCase;
            this.products = products;
        }

        public CartSectionResponseDto(String sectionCase, List<ProductType> productTypeList, String sectionTitle, List<InCartProduct> products, Integer maxCartCount) {
            this.sectionCase = sectionCase;
            this.sectionTitle = sectionTitle;
            this.products = products;
            this.maxCartCount = maxCartCount;
            this.productTypeList = productTypeList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class InCartProduct {
        private ProductType productType;
        private Long cartId;
        private int productNo;
        private String brandName;
        private String productName;
        private String imageUrl;
        private int orderCnt;
        private int stockCnt;
        private int productOptionNo;
        private int price;
        private int immediateDiscountAmt;
        private boolean isRestockNotification;
        private boolean isMultiPurchase;
        private Integer categoryNo;

        public InCartProduct(CartQueryDto cartQueryDto,
                             ProductType productType,
                             String productName, String brandName, String imageUrl,
                             int stockCnt, int orderCnt, int productOptionNo,
                             int salePrice, int immediateDiscountAmt,
                             boolean isRestockNotification, boolean isMultiPurchase,
                             Integer categoryNo) {
            this.productType = productType;
            this.cartId = cartQueryDto.getCartId();
            this.productNo = cartQueryDto.getProductNo();
            this.brandName = brandName;
            this.productName = productName;
            this.imageUrl = imageUrl;
            this.orderCnt = orderCnt;
            this.stockCnt = stockCnt;
            this.productOptionNo = productOptionNo;
            this.price = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.isRestockNotification = isRestockNotification;
            this.isMultiPurchase = isMultiPurchase;
            this.categoryNo = categoryNo;
        }

        public InCartProduct(ProductType productType,
                             int productNo, String productName, String imageUrl,
                             String brandName,
                             int stockCnt, int productOptionNo,
                             int salePrice, int immediateDiscountAmt,
                             boolean isRestockNotification, boolean isMultiPurchase,
                             Integer categoryNo) {
            this.productType = productType;
            this.productNo = productNo;
            this.productName = productName;
            this.imageUrl = imageUrl;
            this.brandName = brandName;
            this.stockCnt = stockCnt;
            this.productOptionNo = productOptionNo;
            this.price = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.isRestockNotification = isRestockNotification;
            this.isMultiPurchase = isMultiPurchase;
            this.categoryNo = categoryNo;
        }

        public boolean getIsRestockNotification() {
            return isRestockNotification;
        }

        public boolean getIsMultiPurchase() {
            return isMultiPurchase;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class WishProduct {
        private ProductType productType;
        private int productNo;
        private String brandName;
        private String productName;
        private String imageUrl;
        private int stockCnt;
        private int productOptionNo;
        private int price;
        private int immediateDiscountAmt;
        private boolean isRestockNotification;
        private boolean isMultiPurchase;
        private Integer categoryNo;
        private Double reviewRating;
        private int reviewCnt;

        public WishProduct(ProductType productType, int productNo,
                           String brandName, String productName,
                           String imageUrl, int stockCnt, int productOptionNo,
                           int price, int immediateDiscountAmt,
                           boolean isRestockNotification, boolean isMultiPurchase,
                           Integer categoryNo,
                           Double reviewRating, int reviewCnt) {
            this.productType = productType;
            this.productNo = productNo;
            this.brandName = brandName;
            this.productName = productName;
            this.imageUrl = imageUrl;
            this.stockCnt = stockCnt;
            this.productOptionNo = productOptionNo;
            this.price = price;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.isRestockNotification = isRestockNotification;
            this.isMultiPurchase = isMultiPurchase;
            this.categoryNo = categoryNo;
            this.reviewRating = reviewRating;
            this.reviewCnt = reviewCnt;
        }
    }
}

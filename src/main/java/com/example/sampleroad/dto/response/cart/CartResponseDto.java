package com.example.sampleroad.dto.response.cart;

import com.example.sampleroad.dto.response.product.IProduct;
import com.example.sampleroad.dto.response.product.ProductDetailResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
public class CartResponseDto {

    private int standardAmt; // 총 상품금액
    private int discountAmt; // 총 할인금액
    private List<OrderProduct> orderProductList;

    public CartResponseDto(int standardAmt, int discountAmt
            , List<OrderProduct> orderProductList) {
        this.standardAmt = standardAmt;
        this.discountAmt = discountAmt;
        this.orderProductList = orderProductList;
    }

    public CartResponseDto(List<OrderProduct> orderProductList) {
        this.orderProductList = orderProductList;
    }

    @NoArgsConstructor
    @Getter
    public static class ResponseDto {
        private int standardAmt; // 총 상품금액
        private int discountAmt; // 총 할인금액
        private int baseDeliveryAmt;// 기본 배송비
        private int aboveDeliveryAmt;// 기준 배송비
        private List<OrderProduct> customKitProducts;
        private List<OrderProduct> nonCustomKitProducts;

        public ResponseDto(int standardAmt, int discountAmt, int baseDeliveryAmt, int aboveDeliveryAmt
                , List<OrderProduct> customKitProducts, List<OrderProduct> nonCustomKitProducts) {
            this.standardAmt = standardAmt;
            this.discountAmt = discountAmt;
            this.baseDeliveryAmt = baseDeliveryAmt;
            this.aboveDeliveryAmt = aboveDeliveryAmt;
            this.customKitProducts = customKitProducts;
            this.nonCustomKitProducts = nonCustomKitProducts;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class OrderProduct {
        private Long cartId;
        private Boolean isCustomKit;
        private int productNo;
        private String brandName;
        private String productName;
        private String imageUrl;
        private List<OrderProductOption> productOptions;
        private List<ProductDetailResponseDto.SampleList> sampleList;

        public OrderProduct(Long cartId, Boolean isCustomKit, int productNo, String brandName, String productName,
                            String imageUrl, List<OrderProductOption> productOptions, List<ProductDetailResponseDto.SampleList> sampleList) {
            this.cartId = cartId;
            this.isCustomKit = isCustomKit;
            this.productNo = productNo;
            this.brandName = brandName;
            this.productName = productName;
            this.imageUrl = imageUrl;
            this.productOptions = productOptions;
            this.sampleList = sampleList;
        }

        public OrderProduct(Long cartId, Boolean isCustomKit, int productNo, String brandName, String productName,
                            String imageUrl, List<OrderProductOption> productOptions) {
            this.cartId = cartId;
            this.isCustomKit = isCustomKit;
            this.productNo = productNo;
            this.brandName = brandName;
            this.productName = productName;
            this.imageUrl = imageUrl;
            this.productOptions = productOptions;
        }

        public OrderProduct(int productNo, List<OrderProductOption> orderProductOptionList) {
            this.productNo = productNo;
            this.productOptions = orderProductOptionList;
        }

        public OrderProduct(Long cartId, boolean isCustomKit, int productNo, String brandName, String productName, String imageUrl) {
            this.cartId = cartId;
            this.isCustomKit = isCustomKit;
            this.productNo = productNo;
            this.brandName = brandName;
            this.productName = productName;
            this.imageUrl = imageUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class InvalidOrderProduct {
        private int productNo;
        private String brandName;
        private String productName;

        public InvalidOrderProduct(int productNo, String brandName, String productName) {
            this.productNo = productNo;
            this.brandName = brandName;
            this.productName = productName;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class OrderProductOption {
        private String optionName;
        private String optionValue;
        private Integer orderCnt;
        private int stockCnt;
        private Integer optionNo;
        private int productNo;
        private int cartNo;
        private int price;
        private int immediateDiscountAmt;

        public OrderProductOption(String optionName, String optionValue, Integer orderCnt, int stockCnt,
                                  Integer optionNo, int productNo, int cartNo, int price, int immediateDiscountAmt) {
            this.optionName = optionName;
            this.optionValue = optionValue;
            this.orderCnt = orderCnt;
            this.stockCnt = stockCnt;
            this.optionNo = optionNo;
            this.productNo = productNo;
            this.cartNo = cartNo;
            this.price = price;
            this.immediateDiscountAmt = immediateDiscountAmt;
        }

        public OrderProductOption(int optionNo, int cartNo, int stockCnt, int orderCnt) {
            this.optionNo = optionNo;
            this.cartNo = cartNo;
            this.stockCnt = stockCnt;
            this.orderCnt = orderCnt;
        }

        public OrderProductOption(int optionNo, int cartNo, int stockCnt) {
            this.optionNo = optionNo;
            this.cartNo = cartNo;
            this.stockCnt = stockCnt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Product implements IProduct {
        private int productNo;
        private int optionNo;
        private int orderCnt;

        public Product(int productNo, int optionNo, int orderCnt) {
            this.productNo = productNo;
            this.optionNo = optionNo;
            this.orderCnt = orderCnt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CreateOrder {
        private List<CartResponseDto.Product> products;
        private int[] cartNos;

        public CreateOrder(List<CartResponseDto.Product> products, int[] cartNos) {
            this.products = products;
            this.cartNos = cartNos;
        }
    }
}

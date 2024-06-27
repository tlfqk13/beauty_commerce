package com.example.sampleroad.dto.response.order;

import com.example.sampleroad.domain.order.OrderType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
public class OrderCancelResponseDto {
    List<OrderCancelResponseDto.SampleKitGroup> sampleKitGroup;
    List<OrderCancelResponseDto.ProductList> customKitList;

    public OrderCancelResponseDto(List<OrderCancelResponseDto.ProductList> customKitList,
                                  List<OrderCancelResponseDto.SampleKitGroup> sampleKitGroup) {
        this.customKitList = customKitList;
        this.sampleKitGroup = sampleKitGroup;
    }

    @NoArgsConstructor
    @Getter
    public static class ProductList {
        private int productNo;
        private String productName;
        private String brandName;
        private String imageUrl;
        private int price;
        private int discountedPrice;

        public ProductList(int productNo, String productName, String brandName, String imageUrl, int price, int discountedPrice) {
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
            this.price = price;
            this.discountedPrice = discountedPrice;
        }

        public ProductList(int productNo, String productName, String brandName, String imageUrl) {
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class SampleKitGroup {
        private String sampleKitImage;
        private String sampleKitName;
        private String sampleKitBrandName;
        private int sampleKitOrderCnt;
        private int sampleKitPrice;
        private int sampleKitImmediatePrice;
        private List<OrderCancelResponseDto.ProductList> sampleList;

        public SampleKitGroup(String sampleKitImage, String sampleKitName,
                              String sampleKitBrandName, int sampleKitOrderCnt,
                              int sampleKitPrice, int sampleKitImmediatePrice,
                              List<OrderCancelResponseDto.ProductList> sampleList) {
            this.sampleKitImage = sampleKitImage;
            this.sampleKitName = sampleKitName;
            this.sampleKitBrandName = sampleKitBrandName;
            this.sampleKitOrderCnt = sampleKitOrderCnt;
            this.sampleKitPrice = sampleKitPrice;
            this.sampleKitImmediatePrice = sampleKitImmediatePrice;
            this.sampleList = sampleList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ProductGroup {
        private String sampleKitImage;
        private String sampleKitName;
        private String sampleKitBrandName;
        private int sampleKitOrderCnt;
        private int sampleKitPrice;
        private int sampleKitImmediatePrice;

        public ProductGroup(String sampleKitImage, String sampleKitName, String sampleKitBrandName,
                            int sampleKitOrderCnt, int sampleKitPrice, int sampleKitImmediatePrice) {
            this.sampleKitImage = sampleKitImage;
            this.sampleKitName = sampleKitName;
            this.sampleKitBrandName = sampleKitBrandName;
            this.sampleKitOrderCnt = sampleKitOrderCnt;
            this.sampleKitPrice = sampleKitPrice;
            this.sampleKitImmediatePrice = sampleKitImmediatePrice;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CalculateCancelOrder {
        private int totalProductAmt;
        private int refundPayAmt;
        private int refundSubPayAmt;
        private String refundType;
        private String refundPayType;
        private String refundTypeLabel;
        private int additionalPayAmt;
        private int refundMainPayAmt;
        private int cartCouponAmt;
        private int productCouponDiscountAmt;
        private int deliveryTotalAmt;

        public CalculateCancelOrder(int totalProductAmt, int refundPayAmt, int refundSubPayAmt, String refundType,
                                    String refundPayType, String refundTypeLabel, int additionalPayAmt,
                                    int refundMainPayAmt, int cartCouponAmt, int productCouponDiscountAmt,
                                    int deliveryTotalAmt) {
            this.totalProductAmt = totalProductAmt;
            this.refundPayAmt = refundPayAmt;
            this.refundSubPayAmt = refundSubPayAmt;
            this.refundType = refundType;
            this.refundPayType = refundPayType;
            this.refundTypeLabel = refundTypeLabel;
            this.additionalPayAmt = additionalPayAmt;
            this.refundMainPayAmt = refundMainPayAmt;
            this.cartCouponAmt = cartCouponAmt;
            this.productCouponDiscountAmt = productCouponDiscountAmt;
            this.deliveryTotalAmt = deliveryTotalAmt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class OrderCancelInfo {
        private OrderType orderType;
        private List<OrderDetailResponseDto.OrderDetailSectionResponseDto> orderDetailSection;
        private List<Integer> orderOptionNos;
        private OrderCancelResponseDto.CalculateCancelOrder calculateCancelOrder;

        public OrderCancelInfo(OrderType orderType,
                               List<OrderDetailResponseDto.OrderDetailSectionResponseDto> orderDetailSection,
                               List<Integer> orderOptionNos,
                               CalculateCancelOrder calculateCancelOrder) {
            this.orderType = orderType;
            this.orderDetailSection = orderDetailSection;
            this.orderOptionNos = orderOptionNos;
            this.calculateCancelOrder = calculateCancelOrder;
        }

        public OrderCancelInfo(List<OrderDetailResponseDto.OrderDetailSectionResponseDto> orderDetailSection,
                               List<Integer> orderOptionNos) {
            this.orderDetailSection = orderDetailSection;
            this.orderOptionNos = orderOptionNos;
        }

        public OrderCancelInfo(OrderType orderType,
                               List<OrderDetailResponseDto.OrderDetailSectionResponseDto> orderDetailSection,
                               CalculateCancelOrder calculateCancelOrder) {
            this.orderType = orderType;
            this.orderDetailSection = orderDetailSection;
            this.calculateCancelOrder = calculateCancelOrder;
        }
    }


}


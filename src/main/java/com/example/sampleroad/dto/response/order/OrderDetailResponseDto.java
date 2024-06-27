package com.example.sampleroad.dto.response.order;

import com.example.sampleroad.domain.claim.ClaimStatus;
import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.domain.product.ProductType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
public class OrderDetailResponseDto {
    @NoArgsConstructor
    @Getter
    @Setter
    // 주문 상세 조회하기
    public static class OrderDetail {
        private OrderResponseDto.OrderInfo orderInfo;
        private OrderResponseDto.ShippingAddress shippingInfo;
        private OrderResponseDto.PayInfo payInfo;
        private List<OrderResponseDto.SampleKitGroup> sampleKitGroup;
        private List<OrderResponseDto.CustomKitGroup> customKitGroup;
        private List<OrderDetailSectionResponseDto> orderDetailSection;
        private List<Integer> orderOptionNos;

        public OrderDetail(OrderResponseDto.OrderInfo orderInfo, OrderResponseDto.OrderDetail orderDetail,
                           OrderResponseDto.PayInfo payInfo,
                           List<OrderResponseDto.SampleKitGroup> sampleKitList,
                           List<OrderResponseDto.CustomKitGroup> customKitGroup) {
            this.orderInfo = orderInfo;
            this.shippingInfo = orderDetail.getShippingInfo();
            this.payInfo = payInfo;
            this.sampleKitGroup = sampleKitList;
            this.customKitGroup = customKitGroup;
        }

        public OrderDetail(OrderResponseDto.OrderInfo orderInfo,
                           OrderResponseDto.ShippingAddress shippingInfo,
                           OrderResponseDto.PayInfo payInfo,
                           List<OrderDetailSectionResponseDto> orderDetailSection,
                           List<Integer> orderOptionNos) {
            this.orderInfo = orderInfo;
            this.shippingInfo = shippingInfo;
            this.payInfo = payInfo;
            this.orderDetailSection = orderDetailSection;
            this.orderOptionNos = orderOptionNos;
        }
    }


    @NoArgsConstructor
    @Getter
    // 주문 상세 조회하기
    public static class NewOrderDetail {
        private OrderResponseDto.OrderInfo orderInfo;
        private OrderResponseDto.ShippingAddress shippingInfo;
        private OrderResponseDto.PayInfo payInfo;
        private List<NewInPayInfoProduct> inPayInfoProductList;

        public NewOrderDetail(OrderResponseDto.OrderInfo orderInfo, OrderResponseDto.ShippingAddress shippingInfo,
                              OrderResponseDto.PayInfo payInfo, List<NewInPayInfoProduct> inPayInfoProductList) {
            this.orderInfo = orderInfo;
            this.shippingInfo = shippingInfo;
            this.payInfo = payInfo;
            this.inPayInfoProductList = inPayInfoProductList;
        }
    }

    @NoArgsConstructor
    @Getter
    // 주문 상세 조회하기
    public static class NewInPayInfoProduct {
        private String productName;
        private String productImgUrl;
        private int productNo;
        private int productOptionNo;
        private int orderOptionNo;
        private int productStandardPrice;
        private int productImmediateDiscountedPrice;
        private int orderCnt;
        private Integer claimNo;
        private OrderStatus orderStatusType;
        private ClaimStatus claimStatusType;
        private String retrieveInvoiceUrl;
        private String brandName;

        public NewInPayInfoProduct(String productName, String productImgUrl, int productNo, int productOptionNo
                , int orderOptionNo, int productStandardPrice, int productImmediateDiscountedPrice
                , int orderCnt, Integer claimNo, OrderStatus orderStatusType, ClaimStatus claimStatusType, String retrieveInvoiceUrl
                , String brandName) {
            this.productName = productName;
            this.productImgUrl = productImgUrl;
            this.productNo = productNo;
            this.productOptionNo = productOptionNo;
            this.orderOptionNo = orderOptionNo;
            this.productStandardPrice = productStandardPrice;
            this.productImmediateDiscountedPrice = productImmediateDiscountedPrice;
            this.orderCnt = orderCnt;
            this.claimNo = claimNo;
            this.orderStatusType = orderStatusType;
            this.claimStatusType = claimStatusType;
            this.retrieveInvoiceUrl = retrieveInvoiceUrl;
            this.brandName = brandName;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class OrderDetailSectionResponseDto {
        private String sectionTitle;
        private List<?> products;

        public OrderDetailSectionResponseDto(String sectionTitle, List<?> products) {
            this.sectionTitle = sectionTitle;
            this.products = products;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class InOrderDetailProduct {
        private ProductType productType;
        private OrderStatus orderStatus;
        private OrderStatus groupPurchaseStatusType; // 팀구매 테그용 상태ㄴ
        private int productNo;
        private String brandName;
        private String productName;
        private String imageUrl;
        private int orderCnt;
        private int productOptionNo;
        private int orderOptionNo; // 리뷰작성에서 사용해야 함
        private int price;
        private int immediateDiscountAmt;
        private Long reviewId;

        public void setGroupPurchaseStatusType(OrderStatus groupPurchaseStatusType) {
            this.groupPurchaseStatusType = groupPurchaseStatusType;
        }

        public InOrderDetailProduct(ProductType productType, OrderStatus orderStatus, int productNo,
                                    String brandName, String productName,
                                    String imageUrl, int orderCnt,
                                    int productOptionNo, int orderOptionNo,
                                    int price, int immediateDiscountAmt,
                                    Long reviewId) {
            this.productType = productType;
            this.orderStatus = orderStatus;
            this.productNo = productNo;
            this.brandName = brandName;
            this.productName = productName;
            this.imageUrl = imageUrl;
            this.orderCnt = orderCnt;
            this.productOptionNo = productOptionNo;
            this.orderOptionNo = orderOptionNo;
            this.price = price;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.reviewId = reviewId;
        }
    }
}

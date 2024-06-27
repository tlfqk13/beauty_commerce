package com.example.sampleroad.dto.response.order;

import com.example.sampleroad.domain.claim.ClaimStatus;
import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.domain.order.OrderType;
import com.example.sampleroad.domain.product.ProductType;
import com.example.sampleroad.dto.response.DeliveryLocationResponseDto;
import com.example.sampleroad.dto.response.product.ProductDetailResponseDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
public class OrderResponseDto {
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Products {
        private Boolean isCustomKit;
        private String orderNo;
        private String orderDate;
        private int orderOptionNo;
        private Integer claimNo;
        private int productNo;
        private String imageUrl;
        private String brandName;
        private int orderCount;
        private OrderStatus orderStatusType;
        private OrderStatus groupPurchaseType;
        private ClaimStatus claimStatusType;
        private int productPrice;
        private int salePrice;
        private String optionTitle;
        private String invoiceNo;
        private String retrieveInvoiceUrl;
        private String deliveryCompanyTypeLabel;
        private List<NextAction> nextActions;

        public Products(Boolean isCustomKit, String orderNo, String orderDate, int orderOptionNo,
                        Integer claimNo, int productNo, String imageUrl, String brandName,
                        int orderCount, OrderStatus orderStatusType, ClaimStatus claimStatusType, int productPrice,
                        int salePrice, String optionTitle, String invoiceNo, String retrieveInvoiceUrl,
                        String deliveryCompanyTypeLabel, List<NextAction> nextActions) {
            this.isCustomKit = isCustomKit;
            this.orderNo = orderNo;
            this.orderDate = orderDate;
            this.orderOptionNo = orderOptionNo;
            this.claimNo = claimNo;
            this.productNo = productNo;
            this.imageUrl = imageUrl;
            this.brandName = brandName;
            this.orderCount = orderCount;
            this.orderStatusType = orderStatusType;
            this.claimStatusType = claimStatusType;
            this.productPrice = productPrice;
            this.salePrice = salePrice;
            this.optionTitle = optionTitle;
            this.invoiceNo = invoiceNo;
            this.retrieveInvoiceUrl = retrieveInvoiceUrl;
            this.deliveryCompanyTypeLabel = deliveryCompanyTypeLabel;
            this.nextActions = nextActions;
        }

        public Products(Boolean isCustomKit, String orderNo, String orderDate, int orderOptionNo,
                        Integer claimNo, int productNo, String imageUrl, String brandName,
                        int orderCount,
                        OrderStatus orderStatusType, OrderStatus groupPurchaseType, ClaimStatus claimStatusType, int productPrice,
                        int salePrice, String optionTitle, String invoiceNo, String retrieveInvoiceUrl,
                        String deliveryCompanyTypeLabel, List<NextAction> nextActions) {
            this.isCustomKit = isCustomKit;
            this.orderNo = orderNo;
            this.orderDate = orderDate;
            this.orderOptionNo = orderOptionNo;
            this.claimNo = claimNo;
            this.productNo = productNo;
            this.imageUrl = imageUrl;
            this.brandName = brandName;
            this.orderCount = orderCount;
            this.orderStatusType = orderStatusType;
            this.groupPurchaseType = groupPurchaseType;
            this.claimStatusType = claimStatusType;
            this.productPrice = productPrice;
            this.salePrice = salePrice;
            this.optionTitle = optionTitle;
            this.invoiceNo = invoiceNo;
            this.retrieveInvoiceUrl = retrieveInvoiceUrl;
            this.deliveryCompanyTypeLabel = deliveryCompanyTypeLabel;
            this.nextActions = nextActions;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class OrderListGroup {
        private OrderType orderType;
        private List<Products> customKitList;
        private List<Products> nonCustomKitList;

        public OrderListGroup(OrderType orderType, List<Products> customKitList, List<Products> nonCustomKitList) {
            this.orderType = orderType;
            this.customKitList = customKitList;
            this.nonCustomKitList = nonCustomKitList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class NewOrderListGroup {
        private OrderType orderType;
        private int totalProductCount;
        private InOrderListRepresentativeProduct orderProduct;

        public NewOrderListGroup(OrderType orderType, int totalProductCount, InOrderListRepresentativeProduct orderProduct) {
            this.orderType = orderType;
            this.totalProductCount = totalProductCount;
            this.orderProduct = orderProduct;
        }

        public NewOrderListGroup(OrderType orderType, InOrderListRepresentativeProduct orderProduct) {
            this.orderType = orderType;
            this.orderProduct = orderProduct;
        }

        public NewOrderListGroup(int totalProductCount, InOrderListRepresentativeProduct orderProduct) {
            this.totalProductCount = totalProductCount;
            this.orderProduct = orderProduct;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class InOrderListProduct {
        private OrderStatus orderStatus;
        private String orderNo;
        private String orderDate;
        private String productName;
        private int productNo;
        private String imageUrl;
        private String brandName;
        private int orderCnt;
        private int price;
        private int immediateDiscountAmt;

        public InOrderListProduct(OrderStatus orderStatus,
                                  String orderNo, String orderDate, String productName,
                                  int productNo, String imageUrl, String brandName,
                                  int orderCnt, int price, int immediateDiscountAmt) {
            this.orderStatus = orderStatus;
            this.orderNo = orderNo;
            this.orderDate = orderDate;
            this.productName = productName;
            this.productNo = productNo;
            this.imageUrl = imageUrl;
            this.brandName = brandName;
            this.orderCnt = orderCnt;
            this.price = price;
            this.immediateDiscountAmt = immediateDiscountAmt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class InOrderListRepresentativeProduct {
        private OrderStatus orderStatus;
        private OrderStatus groupPurchaseType;
        private String orderNo;
        private String orderDate;
        private String productName;
        private int productNo;
        private String imageUrl;
        private String brandName;
        private int orderCnt;
        private int price;
        private int immediateDiscountAmt;

        public InOrderListRepresentativeProduct(InOrderListProduct inOrderListProduct, int price, int immediateDiscountAmt) {
            this.orderStatus = inOrderListProduct.getOrderStatus();
            this.orderNo = inOrderListProduct.getOrderNo();
            this.orderDate = inOrderListProduct.getOrderDate();
            this.productName = inOrderListProduct.getProductName();
            this.productNo = inOrderListProduct.getProductNo();
            this.imageUrl = inOrderListProduct.getImageUrl();
            this.brandName = inOrderListProduct.getBrandName();
            this.orderCnt = inOrderListProduct.getOrderCnt();
            this.price = price;
            this.immediateDiscountAmt = immediateDiscountAmt;
        }

        public InOrderListRepresentativeProduct(InOrderListProduct inOrderListProduct, OrderStatus groupPurchaseType, int price, int immediateDiscountAmt) {
            this.orderStatus = inOrderListProduct.getOrderStatus();
            this.groupPurchaseType = groupPurchaseType;
            this.orderNo = inOrderListProduct.getOrderNo();
            this.orderDate = inOrderListProduct.getOrderDate();
            this.productName = inOrderListProduct.getProductName();
            this.productNo = inOrderListProduct.getProductNo();
            this.imageUrl = inOrderListProduct.getImageUrl();
            this.brandName = inOrderListProduct.getBrandName();
            this.orderCnt = inOrderListProduct.getOrderCnt();
            this.price = price;
            this.immediateDiscountAmt = immediateDiscountAmt;
        }

        public void setGroupPurchaseType(OrderStatus groupPurchaseType) {
            this.groupPurchaseType = groupPurchaseType;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class OrderList {
        private List<Products> customKitList;
        private List<Products> nonCustomKitList;
    }


    @NoArgsConstructor
    @Getter
    public static class OrderListShopby {

        private int totalCount;
        private List<OrderListGroup> items;

        public OrderListShopby(int totalCount, List<OrderListGroup> items) {
            this.totalCount = totalCount;
            this.items = items;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class NewOrderListShopby {

        private int totalCount;
        private List<NewOrderListGroup> items;

        public NewOrderListShopby(int totalCount, List<NewOrderListGroup> items) {
            this.totalCount = totalCount;
            this.items = items;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ShippingAddress {
        private String receiverZipCd;
        private String receiverAddress;
        private String receiverJibunAddress;
        private String receiverDetailAddress;
        private String receiverName;
        private String receiverContact1;
        private String addressName;
        private String deliveryMemo;

        public ShippingAddress(String receiverZipCd, String receiverAddress, String receiverJibunAddress, String receiverDetailAddress,
                               String receiverName, String receiverContact1, String addressName, String deliveryMemo) {
            this.receiverZipCd = receiverZipCd;
            this.receiverAddress = receiverAddress;
            this.receiverJibunAddress = receiverJibunAddress;
            this.receiverDetailAddress = receiverDetailAddress;
            this.receiverName = receiverName;
            this.receiverContact1 = receiverContact1;
            this.addressName = addressName;
            this.deliveryMemo = deliveryMemo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CreateOrderSheet {
        private String orderSheetNo;
        private OrderPaymentPriceResponseDto.PaymentInfo paymentInfo;
        private OrderCalculateCouponResponseDto couponInfo;
        private List<OrderResponseDto.OrderSectionResponseDto> orderProducts;

        public CreateOrderSheet(String orderSheetNo, OrderPaymentPriceResponseDto.PaymentInfo paymentInfo,
                                OrderCalculateCouponResponseDto couponInfo) {
            this.orderSheetNo = orderSheetNo;
            this.paymentInfo = paymentInfo;
            this.couponInfo = couponInfo;
        }

        public CreateOrderSheet(String orderSheetNo, OrderPaymentPriceResponseDto.PaymentInfo paymentInfo,
                                OrderCalculateCouponResponseDto couponInfo,
                                List<OrderResponseDto.OrderSectionResponseDto> orderProducts) {
            this.orderSheetNo = orderSheetNo;
            this.paymentInfo = paymentInfo;
            this.couponInfo = couponInfo;
            this.orderProducts = orderProducts;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class getOrderSheet {
        private List<OrderResponseDto.Orderer> orderer;

        public getOrderSheet(List<OrderResponseDto.Orderer> orderer) {
            this.orderer = orderer;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class Orderer {
        private Boolean isCustomKit;
        private int productNo;
        private String brandName;
        private String productName;
        private List<ProductDetailResponseDto.SampleList> sampleList;

        public Orderer(Boolean isCustomKit, int productNo, String brandName, String productName) {
            this.isCustomKit = isCustomKit;
            this.productNo = productNo;
            this.brandName = brandName;
            this.productName = productName;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ResponseDto {
        private DeliveryLocationResponseDto.DeliveryLocation defaultAddress;
        private List<OrderResponseDto.Orderer> customKitProducts;
        private List<OrderResponseDto.Orderer> nonCustomKitProducts;

        public ResponseDto(DeliveryLocationResponseDto.DeliveryLocation defaultAddress
                , List<OrderResponseDto.Orderer> customKitProducts, List<OrderResponseDto.Orderer> nonCustomKitProducts) {
            this.defaultAddress = defaultAddress;
            this.customKitProducts = customKitProducts;
            this.nonCustomKitProducts = nonCustomKitProducts;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    // 주문 상세 조회하기
    public static class OrderInfo {
        private OrderType orderType;
        private String orderNo;
        private String orderDate;
        private OrderStatus orderStatusType;
        private String retrieveInvoiceUrl;

        public OrderInfo(OrderInfo orderInfo, OrderType orderType) {
            this.orderType = orderType;
            this.orderNo = orderInfo.getOrderNo();
            this.orderDate = orderInfo.getOrderDate();
            this.orderStatusType = orderInfo.getOrderStatusType();
        }

        public OrderInfo(String orderNo, String orderDate, OrderStatus orderStatusType) {
            this.orderNo = orderNo;
            this.orderDate = orderDate;
            this.orderStatusType = orderStatusType;
        }

        public OrderInfo(OrderType orderType, String orderNo, String orderDate, OrderStatus orderStatusType, String retrieveInvoiceUrl) {
            this.orderType = orderType;
            this.orderNo = orderNo;
            this.orderDate = orderDate;
            this.orderStatusType = orderStatusType;
            this.retrieveInvoiceUrl = retrieveInvoiceUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class PayInfo {
        private int totalProductAmount;
        private int deliveryAmount;
        private int standardAmt;
        private int immediateDiscountAmt;
        private int couponDiscountAmount;
        private int pointDiscountAmount;
        private String payTypeLabel;
        private CancelInfo cancelInfo;

        public PayInfo(int totalProductAmount, int deliveryAmount, int standardAmt, int immediateDiscountAmt,
                       int couponDiscountAmount, int pointDiscountAmount, String payTypeLabel) {
            this.totalProductAmount = totalProductAmount;
            this.deliveryAmount = deliveryAmount;
            this.standardAmt = standardAmt;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.couponDiscountAmount = couponDiscountAmount;
            this.pointDiscountAmount = pointDiscountAmount;
            this.payTypeLabel = payTypeLabel;
        }

        public PayInfo(PayInfo payInfo, CancelInfo cancelInfo) {
            this.totalProductAmount = payInfo.getTotalProductAmount();
            this.deliveryAmount = payInfo.getDeliveryAmount();
            this.standardAmt = payInfo.getStandardAmt();
            this.immediateDiscountAmt = payInfo.getImmediateDiscountAmt();
            this.couponDiscountAmount = payInfo.getCouponDiscountAmount();
            this.pointDiscountAmount = payInfo.getPointDiscountAmount();
            this.payTypeLabel = payInfo.getPayTypeLabel();
            this.cancelInfo = cancelInfo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CancelInfo {
        private String claimReasonText;
        private String claimReasonDetail;
        private int refundAmt;

        public CancelInfo(String claimReasonText, String claimReasonDetail, int refundAmt) {
            this.claimReasonText = claimReasonText;
            this.claimReasonDetail = claimReasonDetail;
            this.refundAmt = refundAmt;
        }
    }


    /*        "cancelInfo": {
        //없는 경우에는 null
        "claimReasonText": "배송지를 변경하고 싶어요",
        "claimReasonDetail" : "배송지 바꾸려고 했는데 고객센터 전화도 안받고 해서 그냥 취소 합니다 전화좀 받으세요 진짜 아후;;",
        "refundAmt" : 20000
        }*/


    @NoArgsConstructor
    @Getter
    @Setter
    public static class SampleList {
        private String sampleKitName;
        private int sampleKitProductNo;
        private int productNo;
        private String productName;
        private String brandName;
        private String imageUrl;
        private NextAction nextAction;

        public SampleList(SampleList sampleList, NextAction nextAction) {
            this.sampleKitName = sampleList.getSampleKitName();
            this.sampleKitProductNo = sampleList.getSampleKitProductNo();
            this.productNo = sampleList.getProductNo();
            this.productName = sampleList.getProductName();
            this.brandName = sampleList.getBrandName();
            this.imageUrl = sampleList.getImageUrl();
            this.nextAction = nextAction;
        }

        @QueryProjection
        public SampleList(String sampleKitName, int sampleKitProductNo, int productNo, String productName,
                          String brandName, String imageUrl) {
            this.sampleKitName = sampleKitName;
            this.sampleKitProductNo = sampleKitProductNo;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class customKitList {
        private Long productId;
        private Long customKitId;
        private int productNo;
        private String productName;
        private String brandName;
        private String imageUrl;
        private int price;
        private int immediatePrice;

        @QueryProjection
        public customKitList(Long productId, int productNo, String productName, String brandName, String imageUrl) {
            this.productId = productId;
            this.customKitId = 0L;
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
        }
    }


    @NoArgsConstructor
    @Getter
    // 주문 상세 조회하기
    public static class OrderDetail {
        private OrderResponseDto.OrderInfo orderInfo;
        private ShippingAddress shippingInfo;
        private OrderResponseDto.PayInfo payInfo;
        private List<Integer> orderOptionNos;
        private List<Integer> productNos;
        private List<Integer> claimNos;
        private List<CustomKitGroup> sampleKitGroup;
        private List<customKitList> customKitList;
        private List<NextActionDetail> nextActionList;
        private List<InPayInfoProduct> inPayInfoProductList;

        public OrderDetail(OrderResponseDto.OrderInfo orderInfo, ShippingAddress shippingInfo, OrderResponseDto.PayInfo payInfo,
                           List<Integer> orderOptionNos, List<Integer> productNos, List<Integer> claimNos,
                           List<NextActionDetail> nextActionList, List<InPayInfoProduct> inPayInfoProductList) {
            this.orderInfo = orderInfo;
            this.shippingInfo = shippingInfo;
            this.payInfo = payInfo;
            this.orderOptionNos = orderOptionNos;
            this.productNos = productNos;
            this.claimNos = claimNos;
            this.nextActionList = nextActionList;
            this.inPayInfoProductList = inPayInfoProductList;
        }

        public OrderDetail(OrderDetail orderDetail, List<CustomKitGroup> sampleKitList, List<customKitList> customKitList) {
            this.orderInfo = orderDetail.getOrderInfo();
            this.shippingInfo = orderDetail.getShippingInfo();
            this.payInfo = orderDetail.getPayInfo();
            this.sampleKitGroup = sampleKitList;
            this.customKitList = customKitList;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class SampleKitGroup {
        private String sampleKitImage;
        private String sampleKitBrandName;
        private String sampleKitName;
        private int sampleKitProductNo;
        private String retrieveInvoiceUrl;
        private int sampleKitOrderCnt;
        private int price;
        private int immediatePrice;
        private OrderStatus orderStatusType;
        private OrderStatus groupPurchaseStatusType;
        private ClaimStatus claimStatusType;
        private List<Integer> orderOptionNo; //orderOptionNos
        private List<Integer> claimNo;
        private List<SampleList> sampleList;
        private List<NextActionDetail> nextActions;

        public SampleKitGroup(String sampleKitName, int sampleKitProductNo, List<SampleList> sampleList) {
            // TODO: 2023/11/24 sampleKitName이 있어여한다.주문상세
            this.sampleKitName = sampleKitName;
            this.sampleKitProductNo = sampleKitProductNo;
            this.sampleList = sampleList;
        }

        public SampleKitGroup(String sampleKitImage, String sampleKitBrandName, String sampleKitName,
                              int sampleKitProductNo, String retrieveInvoiceUrl, int sampleKitOrderCnt,
                              int price, int immediatePrice,
                              OrderStatus orderStatusType, OrderStatus groupPurchaseStatusType, ClaimStatus claimStatusType,
                              List<Integer> orderOptionNo,
                              List<Integer> claimNo, List<SampleList> sampleList, List<NextActionDetail> nextActions) {
            this.sampleKitImage = sampleKitImage;
            this.sampleKitBrandName = sampleKitBrandName;
            this.sampleKitName = sampleKitName;
            this.sampleKitProductNo = sampleKitProductNo;
            this.retrieveInvoiceUrl = retrieveInvoiceUrl;
            this.sampleKitOrderCnt = sampleKitOrderCnt;
            this.price = price;
            this.immediatePrice = immediatePrice;
            this.orderStatusType = orderStatusType;
            this.claimStatusType = claimStatusType;
            this.groupPurchaseStatusType = groupPurchaseStatusType;
            this.orderOptionNo = orderOptionNo;
            this.claimNo = claimNo;
            this.sampleList = sampleList;
            this.nextActions = nextActions;
        }
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class CustomKitGroup {
        private String retrieveInvoiceUrl;
        private OrderStatus orderStatusType;
        private ClaimStatus claimStatusType;
        private List<Integer> orderOptionNos;
        private List<Integer> claimNo;
        private List<customKitList> customKitList;
        private Set<String> nextActions;

        public CustomKitGroup(String retrieveInvoiceUrl, OrderStatus orderStatusType, ClaimStatus claimStatusType, List<Integer> orderOptionNos,
                              List<Integer> claimNo, List<customKitList> customKitList, Set<String> nextActions) {
            this.retrieveInvoiceUrl = retrieveInvoiceUrl;
            this.orderStatusType = orderStatusType;
            this.claimStatusType = claimStatusType;
            this.orderOptionNos = orderOptionNos;
            this.claimNo = claimNo;
            this.customKitList = customKitList;
            this.nextActions = nextActions;
        }
    }

    @NoArgsConstructor
    @Getter
    // 주문 상세 조회하기
    public static class NextAction {
        private String nextActionType;

        public NextAction(String nextActionType) {
            this.nextActionType = nextActionType;
        }
    }

    @NoArgsConstructor
    @Getter
    // 주문 상세 조회하기
    public static class NextActionDetail {
        private int productNo;
        private String nextActionType;

        public NextActionDetail(int productNo, String nextActionType) {
            this.productNo = productNo;
            this.nextActionType = nextActionType;
        }
    }

    @NoArgsConstructor
    @Getter
    // 주문 상세 조회하기
    public static class InPayInfoProduct {
        private String productName;
        private String productImgUrl;
        private int productNo;
        private int productStandardPrice;
        private int productImmediateDiscountedPrice;
        private int orderCnt;
        private OrderStatus orderStatusType;
        private ClaimStatus claimStatusType;
        private String retrieveInvoiceUrl;
        private String brandName;

        public InPayInfoProduct(String productName, String productImgUrl, int productNo, int productStandardPrice, int productImmediateDiscountedPrice
                , int orderCnt, OrderStatus orderStatusType, ClaimStatus claimStatusType, String retrieveInvoiceUrl
                , String brandName) {
            this.productName = productName;
            this.productImgUrl = productImgUrl;
            this.productNo = productNo;
            this.productStandardPrice = productStandardPrice;
            this.productImmediateDiscountedPrice = productImmediateDiscountedPrice;
            this.orderCnt = orderCnt;
            this.orderStatusType = orderStatusType;
            this.claimStatusType = claimStatusType;
            this.retrieveInvoiceUrl = retrieveInvoiceUrl;
            this.brandName = brandName;
        }
    }


    @NoArgsConstructor
    @Getter
    public static class OrderProductPriceInfo {
        private String productName;
        private String brandName;
        private int salePrice;
        private int immediateDiscountAmt;
        private int resultPrice;

        public OrderProductPriceInfo(String productName, String brandName, int salePrice, int immediateDiscountAmt, int resultPrice) {
            this.productName = productName;
            this.brandName = brandName;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.resultPrice = resultPrice;
        }
    }

    /**
     * 주문서 생성에서 상품정보 내려주기
     *
     * @param
     * @author sondong-gyu
     * @version 1.0.0
     * @return
     * @date 3/21/24
     **/
    @NoArgsConstructor
    @Getter
    public static class OrderSectionResponseDto {
        private String sectionCase;
        private String sectionTitle;
        private List<InOrderProduct> products;

        public OrderSectionResponseDto(String sectionCase, String sectionTitle, List<InOrderProduct> products) {
            this.sectionCase = sectionCase;
            this.sectionTitle = sectionTitle;
            this.products = products;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class InOrderProduct {
        private ProductType productType;
        private int productNo;
        private int productOptionNo;
        private String brandName;
        private String productName;
        private String imageUrl;
        private int orderCnt;
        private int price;
        private int immediateDiscountAmt;

        public InOrderProduct(ProductType productType, int productNo, int productOptionNo,
                              String brandName,
                              String productName, String imageUrl,
                              int orderCnt, int price, int immediateDiscountAmt) {
            this.productType = productType;
            this.productOptionNo = productOptionNo;
            this.productNo = productNo;
            this.brandName = brandName;
            this.productName = productName;
            this.imageUrl = imageUrl;
            this.orderCnt = orderCnt;
            this.price = price;
            this.immediateDiscountAmt = immediateDiscountAmt;
        }
    }
}

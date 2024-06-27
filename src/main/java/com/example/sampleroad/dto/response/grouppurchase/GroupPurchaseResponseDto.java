package com.example.sampleroad.dto.response.grouppurchase;

import com.example.sampleroad.domain.grouppurchase.GroupPurchaseType;
import com.example.sampleroad.domain.product.ProductType;
import com.example.sampleroad.dto.response.order.OrderCalculateCouponResponseDto;
import com.example.sampleroad.dto.response.order.OrderPaymentPriceResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class GroupPurchaseResponseDto {

    private List<GroupPurchaseResponseDto.SectionInfo> sections;

    public GroupPurchaseResponseDto(List<GroupPurchaseResponseDto.SectionInfo> sections) {
        this.sections = sections;
    }

    @NoArgsConstructor
    @Getter
    public static class SectionInfo {
        private GroupPurchaseType sectionCase;
        private String sectionTitle;
        private List<GroupPurchaseResponseDto.ProductInfo> products;

        public SectionInfo(GroupPurchaseType sectionCase, String sectionTitle, List<ProductInfo> products) {
            this.sectionCase = sectionCase;
            this.sectionTitle = sectionTitle;
            this.products = products;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ProductInfo {
        private int productNo;
        private String productName;
        private String brandName;
        private String productImageUrl;
        private int salePrice;
        private int immediateDiscountAmt;
        private int stockCnt;
        private String endDate;
        private List<String> groupMemberImgUrls;

        public ProductInfo(int productNo, String productName,
                           String brandName, String productImageUrl,
                           int salePrice, int immediateDiscountAmt,
                           int stockCnt, String endDate,
                           List<String> groupMemberImgUrls) {
            this.productNo = productNo;
            this.productName = productName;
            this.brandName = brandName;
            this.productImageUrl = productImageUrl;
            this.salePrice = salePrice;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.stockCnt = stockCnt;
            this.endDate = endDate;
            this.groupMemberImgUrls = groupMemberImgUrls;
        }

        public ProductInfo(String productName, String brandName, String productImageUrl) {
            this.productName = productName;
            this.brandName = brandName;
            this.productImageUrl = productImageUrl;
        }

        public ProductInfo(int productNo, String productName, String deadLineTime, String productImageUrl) {
            this.productNo = productNo;
            this.productName = productName;
            this.endDate = deadLineTime;
            this.productImageUrl = productImageUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CreateOrderSheet {
        private String orderSheetNo;
        private Long roomId;
        private OrderPaymentPriceResponseDto.PaymentInfo paymentInfo;
        private OrderCalculateCouponResponseDto couponInfo;
        private List<GroupPurchaseResponseDto.GroupPurchaseOrderSectionResponseDto> orderProducts;

        public CreateOrderSheet(String orderSheetNo, Long roomId, OrderPaymentPriceResponseDto.PaymentInfo paymentInfo,
                                OrderCalculateCouponResponseDto couponInfo,
                                List<GroupPurchaseResponseDto.GroupPurchaseOrderSectionResponseDto> orderProducts) {
            this.orderSheetNo = orderSheetNo;
            this.roomId = roomId;
            this.paymentInfo = paymentInfo;
            this.couponInfo = couponInfo;
            this.orderProducts = orderProducts;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class GroupPurchaseOrderSectionResponseDto {
        private String sectionCase;
        private String sectionTitle;
        private List<GroupPurchaseResponseDto.InGroupPurchaseProduct> products;

        public GroupPurchaseOrderSectionResponseDto(String sectionCase, String sectionTitle,
                                                    List<GroupPurchaseResponseDto.InGroupPurchaseProduct> products) {
            this.sectionCase = sectionCase;
            this.sectionTitle = sectionTitle;
            this.products = products;
        }
    }


    @NoArgsConstructor
    @Getter
    public static class PurchaseInfo {
        private List<PurchaseInfoProductInfo> purchaseInfo;
        private String noticeImageUrl;

        public PurchaseInfo(List<PurchaseInfoProductInfo> purchaseInfo, String noticeImageUrl) {
            this.purchaseInfo = purchaseInfo;
            this.noticeImageUrl = noticeImageUrl;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class PurchaseInfoProductInfo {
        private int productNo;
        private String productName;
        private String productImageUrl;
        private String endDate;
        private int immediateDiscountAmt;
        private int orderCnt;
        private int remainMemberCnt;
        private String orderNo;

        public PurchaseInfoProductInfo(int productNo, String productName, String endDate,
                                       String productImageUrl, int immediateDiscountAmt,
                                       int orderCnt, int remainMemberCnt,
                                       String orderNo) {
            this.productNo = productNo;
            this.productName = productName;
            this.endDate = endDate;
            this.productImageUrl = productImageUrl;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.orderCnt = orderCnt;
            this.remainMemberCnt = remainMemberCnt;
            this.orderNo = orderNo;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class InGroupPurchaseProduct {
        private ProductType productType;
        private int productNo;
        private int productOptionNo;
        private String brandName;
        private String productName;
        private String imageUrl;
        private int orderCnt;
        private int price;
        private int immediateDiscountAmt;

        public InGroupPurchaseProduct(ProductType productType,
                                      int productNo, int productOptionNo,
                                      String productName, String brandName,
                                      String imageUrl,
                                      int price, int immediateDiscountAmt, int orderCnt) {
            this.productType = productType;
            this.productNo = productNo;
            this.productOptionNo = productOptionNo;
            this.productName = productName;
            this.brandName = brandName;
            this.imageUrl = imageUrl;
            this.price = price;
            this.immediateDiscountAmt = immediateDiscountAmt;
            this.orderCnt = orderCnt;
        }
    }
}

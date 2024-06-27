package com.example.sampleroad.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
public class PaymentRequestDto {
    private String orderSheetNo;
    private PaymentRequestDto.ShippingAddress shippingAddress;
    private Boolean member;
    private PaymentRequestDto.Orderer orderer;
    private String pgType;
    private String payType;
    private String orderTitle;
    private int subPayAmt;
    private String clientReturnUrl;
    private BankAccountToDeposit bankAccountToDeposit;
    private String deliveryMemo;
    private Coupons coupons;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class ShippingAddress {
        private int addressNo;
        private String receiverZipCd;
        private String receiverAddress;
        private String receiverJibunAddress;
        private String receiverDetailAddress;
        private String receiverName;
        private String addressName;
        private String receiverContact1;
    }

    @NoArgsConstructor
    @Getter
    public static class Orderer {
        private String ordererName;
        private String ordererEmail;
        private String ordererContact1;
        private String ordererContact2;
    }

    @NoArgsConstructor
    @Getter
    public static class BankAccountToDeposit {
        private String bankCode;
        private String bankAccount;
        private String bankDepositorName;
    }

    @NoArgsConstructor
    @Getter
    public static class Coupons {
        private int cartCouponIssueNo;
        private String promotionCode;
        private List<CouponRequestDto.ProductCoupons> productCoupons;

        @NoArgsConstructor
        @Getter
        public static class ProductCoupons {
            private int productNo;
            private int couponIssueNo;
        }
    }
}

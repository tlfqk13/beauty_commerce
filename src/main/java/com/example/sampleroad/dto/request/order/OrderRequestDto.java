package com.example.sampleroad.dto.request.order;

import com.example.sampleroad.domain.claim.ClaimReasonType;
import com.example.sampleroad.domain.product.ProductType;
import com.example.sampleroad.dto.request.CouponRequestDto;
import com.example.sampleroad.dto.response.product.IProduct;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class OrderRequestDto {

    @NoArgsConstructor
    @Getter
    public static class GroupPurchaseOrder {
        private List<Product> products;
        private Long roomId;
    }

    @NoArgsConstructor
    @Getter
    public static class CreateOrder {
        private List<Product> products;
        private int[] cartNos;

        public CreateOrder(List<Product> products) {
            this.products = products;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CalculateOrder {
        private List<Product> products;
        private CreateDeliveryLocation deliveryLocation;
        private CouponRequestDto.CouponCalculate couponInfo;
        private int[] cartNos;
    }

    @NoArgsConstructor
    @Getter
    public static class CreateDeliveryLocation {
        private String receiverName;
        private String addressName;
        private String receiverContact;
        private String receiverZipCode;
        private String receiverAddress;
        private String receiverJibunAddress;
        private String receiverDetailAddress;
    }

    @NoArgsConstructor
    @Getter
    public static class Product implements IProduct {
        private int productNo;
        private int optionNo;
        private int orderCnt;
    }

    @NoArgsConstructor
    @Getter
    public static class CancelOrder {
        private ClaimReasonType claimReasonType;
        private String claimReasonDetail;
        private OrderRequestDto.BankAccountInfo bankAccountInfo;
        private String orderNo;
        private Boolean isRestoreCart;
        private Boolean isCustomKit;
        private List<Integer> orderOptionNos;

        public Boolean getIsRestoreCart() {
            return isRestoreCart;
        }

        public Boolean getIsCustomKit() {
            return isCustomKit;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CancelOrderProductInfo {
        private boolean isCustomKit;
        private String orderNo;
        private List<Integer> orderOptionNos;

        public boolean getIsCustomKit() {
            return isCustomKit;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class BankAccountInfo {
        private String bankAccount;
        private String bankDepositorName;
        private String bank;
        private String bankName;
    }

    @NoArgsConstructor
    @Getter
    public static class ClaimedProductOptions {
        private int orderProductOptionNo;
        private int productCnt;

        public ClaimedProductOptions(int orderProductOptionNo, int productCnt) {
            this.orderProductOptionNo = orderProductOptionNo;
            this.productCnt = productCnt;
        }
    }


    @NoArgsConstructor
    @Getter
    public static class OrderOptionNos {
        private List<Integer> orderOptionNos;
    }

    @NoArgsConstructor
    @Getter
    public static class PaymentConfirm {
        private ProductType productType;
        private String orderNo;
        private String result;
        private Long roomId;
    }

}

package com.example.sampleroad.dto.response.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OrderPaymentPriceResponseDto {
    @NoArgsConstructor
    @Getter
    public static class PaymentInfo{
        private DeliveryCondition deliveryCondition;
        private int cartAmt;
        private int cartCouponAmt;
        private int deliveryCouponAmt;
        private int paymentAmt; // 즉시할인 된 결제 총액 (결제 예상금액)
        private int productAmt; // 즉시할인된 상품들 합한 금액
        private int productCouponAmt; //
        private int totalAdditionalDiscountAmt;
        private int totalImmediateDiscountAmt; // 즉시 할인 금액
        private int totalStandardAmt; // 즉시할인 안된 모든 상품 금액

        public PaymentInfo(DeliveryCondition deliveryCondition, int cartAmt, int cartCouponAmt,
                           int deliveryCouponAmt, int paymentAmt, int productAmt, int productCouponAmt,
                           int totalAdditionalDiscountAmt, int totalImmediateDiscountAmt, int totalStandardAmt) {
            this.deliveryCondition = deliveryCondition;
            this.cartAmt = cartAmt;
            this.cartCouponAmt = cartCouponAmt;
            this.deliveryCouponAmt = deliveryCouponAmt;
            this.paymentAmt = paymentAmt;
            this.productAmt = productAmt;
            this.productCouponAmt = productCouponAmt;
            this.totalAdditionalDiscountAmt = totalAdditionalDiscountAmt;
            this.totalImmediateDiscountAmt = totalImmediateDiscountAmt;
            this.totalStandardAmt = totalStandardAmt;
        }
    }
    @NoArgsConstructor
    @Getter
    public static class DeliveryCondition{
        private int deliveryAmt; // 배송비
        private int aboveDeliveryAmt; // 얼마이상이면 배송비 무료
        private int baseDeliveryAmt; // 기본 배송비
        private int remoteDeliveryAmt; // 도서 산간 추가 배송비

        public DeliveryCondition(int deliveryAmt, int aboveDeliveryAmt,
                                 int baseDeliveryAmt, int remoteDeliveryAmt) {
            this.deliveryAmt = deliveryAmt;
            this.aboveDeliveryAmt = aboveDeliveryAmt;
            this.baseDeliveryAmt = baseDeliveryAmt;
            this.remoteDeliveryAmt = remoteDeliveryAmt;
        }
    }

}

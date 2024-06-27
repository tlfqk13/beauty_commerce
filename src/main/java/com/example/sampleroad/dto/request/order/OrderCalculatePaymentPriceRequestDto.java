package com.example.sampleroad.dto.request.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class OrderCalculatePaymentPriceRequestDto {

    private List<ShippingAddresses> shippingAddresses;

    @NoArgsConstructor
    @Getter
    public static class ShippingAddresses{
        private Boolean useDefaultAddress;
        private OrderCalculatePaymentPriceRequestDto.ShippingAddress shippingAddress;
        private List<OrderCalculatePaymentPriceRequestDto.PayProductParams> payProductParams;

        public ShippingAddresses(Boolean useDefaultAddress,
                                ShippingAddress shippingAddress, List<PayProductParams> payProductParams) {
            this.useDefaultAddress = useDefaultAddress;
            this.shippingAddress = shippingAddress;
            this.payProductParams = payProductParams;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ShippingAddress{
        private String receiverName;
        private String countryCd;
        private String receiverZipCd;
        private String receiverAddress;
        private String receiverDetailAddress;
        private String receiverJibunAddress;
        private String receiverContact1;

        public ShippingAddress(String receiverName, String countryCd, String receiverZipCd, String receiverAddress,
                               String receiverDetailAddress, String receiverJibunAddress, String receiverContact1) {
            this.receiverName = receiverName;
            this.countryCd = countryCd;
            this.receiverZipCd = receiverZipCd;
            this.receiverAddress = receiverAddress;
            this.receiverDetailAddress = receiverDetailAddress;
            this.receiverJibunAddress = receiverJibunAddress;
            this.receiverContact1 = receiverContact1;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class PayProductParams{
        private int productNo;
        private int optionNo;
        private int orderCnt;

        public PayProductParams(int productNo, int optionNo, int orderCnt) {
            this.productNo = productNo;
            this.optionNo = optionNo;
            this.orderCnt = orderCnt;
        }
    }

}

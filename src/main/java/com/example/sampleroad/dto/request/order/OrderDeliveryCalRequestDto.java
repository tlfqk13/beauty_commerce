package com.example.sampleroad.dto.request.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OrderDeliveryCalRequestDto {
    private OrderDeliveryCalRequestDto.ShippingAddress addressRequest;

    @NoArgsConstructor
    @Getter
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
}

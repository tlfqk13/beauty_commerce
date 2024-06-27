package com.example.sampleroad.dto.response.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OrderMadeResponseDto {

    String orderSheetNo;
    OrderCalculateCouponResponseDto orderCalculateCouponResponseDto;

    public OrderMadeResponseDto(String orderSheetNo, OrderCalculateCouponResponseDto orderCalculateCouponResponseDto) {
        this.orderSheetNo = orderSheetNo;
        this.orderCalculateCouponResponseDto = orderCalculateCouponResponseDto;
    }
}

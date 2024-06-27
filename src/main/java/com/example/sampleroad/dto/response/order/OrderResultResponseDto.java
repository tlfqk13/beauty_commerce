package com.example.sampleroad.dto.response.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class OrderResultResponseDto {
    private OrderResponseDto.OrderInfo orderInfo;
    private OrderResponseDto.ShippingAddress shippingInfo;
    private OrderResponseDto.PayInfo payInfo;
    private List<OrderDetailResponseDto.OrderDetailSectionResponseDto> orderDetailSection;

    public OrderResultResponseDto(OrderResponseDto.OrderInfo orderInfo,
                                  OrderResponseDto.ShippingAddress shippingInfo,
                                  OrderResponseDto.PayInfo payInfo,
                                  List<OrderDetailResponseDto.OrderDetailSectionResponseDto> orderDetailSection) {
        this.orderInfo = orderInfo;
        this.shippingInfo = shippingInfo;
        this.payInfo = payInfo;
        this.orderDetailSection = orderDetailSection;
    }
}

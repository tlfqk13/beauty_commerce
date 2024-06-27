package com.example.sampleroad.dto.response.order;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OrdersQueryDto {
    private Long orderId;
    private Long memberId;

    @QueryProjection
    public OrdersQueryDto(Long orderId, Long memberId) {
        this.orderId = orderId;
        this.memberId = memberId;
    }

    public OrdersQueryDto(Long orderId) {
        this.orderId = orderId;
    }

    @NoArgsConstructor
    @Getter
    public static class OrderCntQueryDto extends OrdersQueryDto {
        private String orderNo;
        private int orderCnt;

        @QueryProjection
        public OrderCntQueryDto(Long orderId, String orderNo, int orderCnt) {
            super(orderId);
            this.orderNo = orderNo;
            this.orderCnt = orderCnt;
        }
    }
}

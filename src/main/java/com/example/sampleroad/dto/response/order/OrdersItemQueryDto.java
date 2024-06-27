package com.example.sampleroad.dto.response.order;

import com.example.sampleroad.domain.CategoryType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class OrdersItemQueryDto {

    private Long ordersItemId;
    private int orderOptionNo;
    private Long productId;
    private int productNo;
    private int productOptionNo;
    private int productCnt;
    private String productName;
    private String brandName;
    private String productImgUrl;
    private String orderNo;
    private CategoryType categoryType;

    @QueryProjection
    public OrdersItemQueryDto(Long ordersItemId, int orderOptionNo, Long productId, int productNo,
                              int productOptionNo, int productCnt,
                              String productName, String brandName, String productImgUrl,
                              String orderNo,
                              CategoryType categoryType) {
        this.ordersItemId = ordersItemId;
        this.orderOptionNo = orderOptionNo;
        this.productId = productId;
        this.productNo = productNo;
        this.productOptionNo = productOptionNo;
        this.productCnt = productCnt;
        this.productName = productName;
        this.brandName = brandName;
        this.productImgUrl = productImgUrl;
        this.orderNo = orderNo;
        this.categoryType = categoryType;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class OrderIdByOrdersItem {
        private Long orderId;

        @QueryProjection
        public OrderIdByOrdersItem(Long orderId) {
            this.orderId = orderId;
        }
    }
}

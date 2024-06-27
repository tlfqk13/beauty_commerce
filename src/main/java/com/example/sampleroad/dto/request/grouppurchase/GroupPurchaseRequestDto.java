package com.example.sampleroad.dto.request.grouppurchase;

import com.example.sampleroad.dto.request.order.OrderRequestDto;
import com.example.sampleroad.dto.response.product.IProduct;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class GroupPurchaseRequestDto {

    @NoArgsConstructor
    @Getter
    public static class CreateOrder{
        private List<OrderRequestDto.Product> products;
        private Long roomId;
    }

    @NoArgsConstructor
    @Getter
    public static class Product implements IProduct {
        private int productNo;
        private int optionNo;
        private int orderCnt;
    }
}

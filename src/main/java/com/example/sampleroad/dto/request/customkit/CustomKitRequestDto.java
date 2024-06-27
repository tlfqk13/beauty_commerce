package com.example.sampleroad.dto.request.customkit;

import com.example.sampleroad.dto.response.product.IProduct;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CustomKitRequestDto {
    @NoArgsConstructor
    @Getter
    public static class AddCartProduct implements IProduct {
        private int productNo;
        private int optionNo;
        private int orderCnt;

        public AddCartProduct(int productNo, int optionNo, int orderCnt) {
            this.productNo = productNo;
            this.optionNo = optionNo;
            this.orderCnt = orderCnt;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class DeleteCartProduct {
        private int productNo;
        private int optionNo;
        private int orderCnt;
    }

    @NoArgsConstructor
    @Getter
    public static class CreateOrder {
        private List<CustomKitRequestDto.AddCartProduct> products;
        public CreateOrder(List<AddCartProduct> products) {
            this.products = products;
        }
    }

}

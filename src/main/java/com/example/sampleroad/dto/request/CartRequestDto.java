package com.example.sampleroad.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
public class CartRequestDto {

    @NoArgsConstructor
    @Getter
    @Setter
    public static class AddToCart {
        private int productNo;
        private int optionNo;
        private int orderCnt;
        private boolean isCustomKit;
        private OptionInputs[] optionInputs;

        public boolean getIsCustomKit() {
            return isCustomKit;
        }

        public AddToCart(int productNo, int optionNo, int orderCnt, OptionInputs[] optionInputs) {
            this.productNo = productNo;
            this.optionNo = optionNo;
            this.orderCnt = orderCnt;
            this.optionInputs = optionInputs;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class OptionInputs {
        private String inputLabel;
        private String inputValue;
    }

    @NoArgsConstructor
    @Getter
    public static class UpdateCart {
        private int orderCnt;
        private Integer optionNo;
        private Integer productOptionNo;
        private OptionInputs[] optionInputs;

        public UpdateCart(int orderCnt, Integer optionNo, Integer productOptionNo, OptionInputs[] optionInputs) {
            this.orderCnt = orderCnt;
            this.optionNo = optionNo;
            this.productOptionNo = optionNo;
            this.optionInputs = optionInputs;
        }

        public UpdateCart(int orderCnt, OptionInputs[] optionInputs) {
            this.orderCnt = orderCnt;
            this.optionInputs = optionInputs;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class UpdateCartShopByRequest {
        private int cartNo;
        private int orderCnt;
        private int optionNo;
        private OptionInputs[] optionInputs;

        public UpdateCartShopByRequest(int cartNo, int orderCnt, int optionNo, OptionInputs[] optionInputs) {
            this.cartNo = cartNo;
            this.orderCnt = orderCnt;
            this.optionNo = optionNo;
            this.optionInputs = optionInputs;
        }

        public UpdateCartShopByRequest(int cartNo, int orderCnt, OptionInputs[] optionInputs) {
            this.cartNo = cartNo;
            this.orderCnt = orderCnt;
            this.optionInputs = optionInputs;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class CartIdsDto {
        private List<Long> cartIds;
    }

    @NoArgsConstructor
    @Getter
    public static class ProductNosDto {
        private List<Integer> productNos;
    }
}

package com.example.sampleroad.dto.response.cart;

public interface ICartProductInfo {
    int getProductNo();
    String getProductName();
    String getBrandName();
    String getImageUrl();
    String getDisplayCategoryNo();
    int getStockCnt();
    int getSalePrice();
    int getImmediateDiscountAmt();
}

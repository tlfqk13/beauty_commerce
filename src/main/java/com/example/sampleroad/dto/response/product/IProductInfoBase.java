package com.example.sampleroad.dto.response.product;

public interface IProductInfoBase {
    int getProductNo();
    String getProductName();
    String getBrandName();
    String getImgUrl();
    int getSalePrice();
    int getImmediateDiscountAmt();
    int getStockCnt();
}

package com.example.sampleroad.domain.claim;

public enum ClaimReasonType {
    CHANGE_MIND,//단순변심,
    WRONG_PRODUCT_DETAIL, // 상품상세 정보와 다름
    DELAY_DELIVERY,//판매자 배송 지연
    DEFECTIVE_PRODUCT,// 상품불량/파손
    WRONG_DELIVERY, // 배송누락/오배송
    OUT_OF_STOCK,// 상품 품절/재고 없음
    OTHERS_BUYER, // 기타(구매자 귀책)
    OTHERS_SELLER // 기타(판매자 귀책)
}

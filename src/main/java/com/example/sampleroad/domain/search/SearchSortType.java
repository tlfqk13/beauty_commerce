package com.example.sampleroad.domain.search;

public enum SearchSortType {
    POPULAR,// 판매인기순 판매가 및 인기도(1주일간 구매수량, 상품후기점수, 좋아요 수 등)
    RECENT_PRODUCT,// 최근등록순
    DISCOUNTED_PRICE,// 가격순
    LOW_PRICE,// 낮은가격순
    HIGH_PRICE,// 높은가격순
    DISCOUNT_RATE,// 할인율순
    SALE_YMD, //판매일자순
    DEFAULT, //업데이트안해서 아무것도 안보내면 처리
}

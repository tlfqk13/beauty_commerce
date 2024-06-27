package com.example.sampleroad.domain.popup;

public enum PopUpDataType {
    MY_COUPON, // 쿠폰함 > [내 쿠폰]으로 이동 (인서트 쿠폰) 화면 이동만
    DOWNLOAD_COUPON,// 쿠폰함 > [쿠폰받기]로 이동 (다운로드 쿠폰) 화면 이동만
    COUPON_LIST,
    REVIEW, // 리뷰탭
    SURVEY, // 리뷰탭
    GroupPurchase,
    EVENT_OUT, // 외부 링크 이동
    POPUP_DETAIL, // 팝업 상세 페이지로 이동 (추석 공지 페이지처럼)
    BANNER_DETAIL, // 내부 공지(배너 상세 페이지)로 이동
    PRODUCT_DETAIL,// 제품 상세 페이지로 이동 화면 이동만
    DISPLAY_DETAIL, // 해당 기획전 페이지로 이동
    EXPERIENCE_DETAIL, // 해당 체험단 페이지로 이동
}

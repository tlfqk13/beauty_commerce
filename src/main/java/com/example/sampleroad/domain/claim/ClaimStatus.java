package com.example.sampleroad.domain.claim;

public enum ClaimStatus {
    // 클레임 상태 : 취소신청, 취소대기, 취소완료, 교환신청,
    // 교환처리, 교환완료, 반품신청, 반품처리, 반품완료
    CANCEL_REQUEST,// 취소신청(승인대기)
    CANCEL_PROC_REQUEST_REFUND, //취소처리(환불보류)
    CANCEL_PROC_WAITING_REFUND, //취소처리(환불대기)
    CANCEL_NO_REFUND,// 취소완료(환불없음)
    CANCEL_DONE,//취소완료
    EXCHANGE_REQUEST,// 교환신청(승인대기)
    EXCHANGE_REJECT_REQUEST,// 교환처리(철회대기)
    EXCHANGE_PROC_BEFORE_RECEIVE,// 교환처리(수거진행)
    EXCHANGE_PROC_REQUEST_PAY,// 교환처리(결제대기)
    EXCHANGE_PROC_REQUEST_REFUND,// 교환처리(환불보류)
    EXCHANGE_PROC_WAITING,// 교환처리(처리대기)
    EXCHANGE_PROC_WAITING_PAY,// 교환처리(입금처리대기)
    EXCHANGE_PROC_WAITING_REFUND,// 교환처리(환불대기)
    RETURN_REQUEST,//  반품신청(승인대기)
    RETURN_REJECT_REQUEST,//  반품신청 (철회대기)
    RETURN_PROC_BEFORE_RECEIVE,//  반품처리(수거진행)
    RETURN_PROC_REQUEST_REFUND,//  반품처리(환불보류)
    RETURN_PROC_WAITING_REFUND,//  반품처리(환불대기)
    RETURN_REFUND_AMT_ADJUST_REQUESTED,//  반품처리(조정요청)
    RETURN_DONE,//  반품완료(환불완료)
    RETURN_NO_REFUND,//  반품완료(환불없음)
    EXCHANGE_DONE,// 교환완료 (차액없음)
    EXCHANGE_DONE_PAY_DONE,// 교환완료 (결제완료)
    EXCHANGE_DONE_REFUND_DONE,// 교환완료(환불완료)
    CANCEL_PROCESSING,//취소처리중
    RETURN_PROCESSING,//반품처리중
    EXCHANGE_WAITING,//교환대기중
    EXCHANGE_PROCESSING,//교환처리중
}

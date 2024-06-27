package com.example.sampleroad.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ErrorCustomException extends RuntimeException{
    private ErrorCode errorCode;

    public ErrorCustomException(ErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }

    public ErrorCustomException(ErrorCode code, List<String> invalidOrderProductNames) {
        super(code.getMessage() + invalidOrderProductNames + " 해당 상품이 품절되었습니다. 해당 상품을 제외하고 주문 해주세요");
        this.errorCode = code;
    }
}
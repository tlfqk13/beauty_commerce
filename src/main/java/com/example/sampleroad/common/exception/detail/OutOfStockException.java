package com.example.sampleroad.common.exception.detail;

import com.example.sampleroad.common.exception.ErrorCode;
import lombok.Getter;

import java.util.List;
@Getter
public class OutOfStockException extends RuntimeException {
    private ErrorCode errorCode;

    public OutOfStockException(ErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }

    public OutOfStockException(ErrorCode code, List<String> invalidOrderProductNames) {
        super(invalidOrderProductNames + " 해당 상품이 품절되었습니다. 해당 상품을 제외하고 주문 해주세요");
        this.errorCode = code;
    }
}

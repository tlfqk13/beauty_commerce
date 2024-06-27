package com.example.sampleroad.common.exception.detail;

import com.example.sampleroad.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class PurchaseConditionProductException extends RuntimeException {
    private ErrorCode errorCode;

    public PurchaseConditionProductException(ErrorCode code) {
        super(code.getMessage());
        this.errorCode = code;
    }

    public PurchaseConditionProductException(ErrorCode code, String productName) {
        super(productName + " 상품은 " + "1만원" + " 구매조건이 있습니다");
        this.errorCode = code;
    }

}

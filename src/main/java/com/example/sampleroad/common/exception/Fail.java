package com.example.sampleroad.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Fail {
    private String message;
    private int status;
    private String code;

    public Fail(final ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
        this.status = errorCode.getStatusCode();
    }

    public Fail(final String message){
        this.message = message;
        this.status = 400;
    }

    public Fail(final ErrorCode errorCode,String message){
        this.message = message;
        this.code = errorCode.getCode();
        this.status = errorCode.getStatusCode();
    }
}
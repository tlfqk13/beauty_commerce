package com.example.sampleroad.common.exception;

import lombok.Getter;

@Getter
public class ErrorShopByException extends RuntimeException{
    private int statusCode;
    private String code;
    private String message;

    public ErrorShopByException(int statusCode, String code, String message){
        this.statusCode = statusCode;
        this.code = code;
        this.message = message;
    }
}
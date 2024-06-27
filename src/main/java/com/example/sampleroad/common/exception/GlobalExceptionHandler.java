package com.example.sampleroad.common.exception;

import com.example.sampleroad.common.exception.detail.OutOfStockException;
import com.example.sampleroad.common.exception.detail.PurchaseConditionProductException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<Fail> handleNotSupportedRequestErrorException(HttpRequestMethodNotSupportedException ex) {
        log.info("handleNotSupportedRequestErrorException");
        Fail restApiException = new Fail("Request를 잘못입력했습니다.");
        log.error(ex.getMessage());
        return new ResponseEntity<>(restApiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ErrorShopByException.class})
    public ResponseEntity<Fail> shopByErrorException(ErrorShopByException ex) {
        Fail apiException = new Fail(ex.getMessage());
        return new ResponseEntity<>(apiException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {ErrorCustomException.class}) // 해당 예외를 처리하기 위한 메소드를 정의합니다.
    public ResponseEntity<Fail> handleCustomErrorException(ErrorCustomException ex) {
        Fail apiException = new Fail(ex.getErrorCode());
        return new ResponseEntity<>(apiException, ex.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(value = {OutOfStockException.class}) // 해당 예외를 처리하기 위한 메소드를 정의합니다.
    public ResponseEntity<Fail> handleOutOfStockErrorException(OutOfStockException ex) {
        Fail apiException = new Fail(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(apiException, ex.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(value = {PurchaseConditionProductException.class}) // 해당 예외를 처리하기 위한 메소드를 정의합니다.
    public ResponseEntity<Fail> handleOPurchaseConditionProductErrorException(PurchaseConditionProductException ex) {
        Fail apiException = new Fail(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(apiException, ex.getErrorCode().getHttpStatus());

    }
}
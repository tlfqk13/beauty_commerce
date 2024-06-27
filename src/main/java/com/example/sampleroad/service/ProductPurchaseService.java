package com.example.sampleroad.service;

import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.dto.response.PaymentResponseDto;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductPurchaseService {

    private final ProductService productService;

    public PaymentResponseDto.Confirm successPayment() throws UnirestException, ParseException {
        PaymentResponseDto.RecommendProductList recommendProductList = productService.getRecommendProductList();
        return new PaymentResponseDto.Confirm(null,recommendProductList);
    }

    public Product getProduct(int productNo) {
        return productService.getProduct(productNo);
    }
}
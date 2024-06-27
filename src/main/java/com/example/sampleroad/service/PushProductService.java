package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.product.Product;
import com.example.sampleroad.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PushProductService {

    private final ProductRepository productRepository;

    public Product getProduct(int productNo) {
        return productRepository.findByProductNoAndProductInvisible(productNo, false)
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}

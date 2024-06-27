package com.example.sampleroad.controller;

import com.example.sampleroad.dto.request.ProductRequestDto;
import com.example.sampleroad.dto.response.product.ProductDetailResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.ProductService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = {"상품 관련 api Controller"})
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/product-info/{productNo}")
    @ApiOperation(value = "상품 상세 조회")
    public ProductDetailResponseDto.ProductInfo getProductInfo(@AuthenticationPrincipal UserDetailsImpl userDetail,
                                                               @PathVariable int productNo) throws UnirestException, ParseException {
        return productService.getProductInfo(userDetail, productNo);
    }

    /**
     * 회원용 최근 본 상품 조회
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/11/13
     **/
    @PostMapping("/api/product-recent")
    @ApiOperation(value = "회원용 최근 본 상품 조회")
    public ProductDetailResponseDto.RecentProducts getRecentProduct(@RequestBody ProductRequestDto.RecentProducts productRequestDto) throws UnirestException, ParseException {
        return productService.getRecentProduct(productRequestDto);
    }
}

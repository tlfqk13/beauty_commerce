package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.dto.request.CouponRequestDto;
import com.example.sampleroad.dto.request.order.OrderCalculateCouponRequestDto;
import com.example.sampleroad.dto.response.coupon.CouponResponseDto;
import com.example.sampleroad.dto.response.order.OrderCalculateCouponResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.CouponService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"쿠폰 관련 api Controller"})
public class CouponController {

    private final CouponService couponService;

    // 재구매 쿠폰 - 장바구니 쿠폰

    @GetMapping("/api/coupons")
    @ApiOperation(value = "발급 가능한 쿠폰 조회하기")
    public CouponResponseDto.DownloadAbleCoupon getAllCoupons(@AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        return couponService.getAllCoupons(userDetails);
    }

    @GetMapping("/api/my-coupons")
    @ApiOperation(value = "내가 발급받은 쿠폰 조회하기")
    public CouponResponseDto.Coupon getAllMyCoupons(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestParam String usable) throws UnirestException, ParseException {
        return couponService.getAllMyCoupons(userDetails, usable);
    }

    @PostMapping("/api/register-code")
    @ApiOperation(value = "코드로 쿠폰 발급하기")
    public CouponResponseDto.RegisterCouponInfo issueCouponUsingPromotionCode(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                              @RequestBody CouponRequestDto.PromotionCode dto) throws UnirestException, ParseException {
        return couponService.issueCouponUsingPromotionCode(userDetails, dto.getPromotionCode());
    }

    @PostMapping("/api/coupons/{couponNo}/download")
    @ApiOperation(value = "해당 쿠폰 다운로드하기")
    public CouponResponseDto.RegisterCouponInfo downloadCoupon(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                               @PathVariable String couponNo) throws UnirestException, ParseException {
        return couponService.downloadCoupon(userDetails, couponNo);
    }

    @GetMapping("/api/coupons/products/{productNo}")
    @ApiOperation(value = "상품 번호로 발급 가능한 쿠폰 조회하기")
    public CouponResponseDto.Coupon getProductDownloadableCoupon(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                  @PathVariable int productNo) throws UnirestException, ParseException {
        return couponService.getProductDownloadableCoupon(userDetails, productNo);
    }


    @PostMapping("/api/coupons/products/{productNo}/download")
    @ApiOperation(value = "상품번호로 쿠폰 다운 받기")
    public CouponResponseDto.ProductAllCoupon downloadProductCoupon(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                    @PathVariable int productNo) throws UnirestException, ParseException {
        return couponService.downloadProductCouponAll(userDetails, productNo);
    }

    @PostMapping("/api/order-sheet/{orderSheetNo}/coupons/calculate")
    @ApiOperation(value = "쿠폰 적용 금액 계산하기")
    public OrderCalculateCouponResponseDto.CalculateCouponResult calculatePaymentPriceByCoupon(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                               @RequestBody OrderCalculateCouponRequestDto dto,
                                                                                               @PathVariable String orderSheetNo) throws UnirestException, ParseException {

        return couponService.calculatePaymentPriceByCoupon(userDetails, dto, orderSheetNo);

    }

    @GetMapping("/api/coupons/{couponNo}/targets")
    @ApiOperation(value = "쿠폰번호로 적용 대상 조회하기")
    public List<CouponResponseDto.CouponTargetDto> getCouponTarget(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable int couponNo,
                                                                   @RequestParam(defaultValue = CustomValue.pageNumber) int pageNumber,
                                                                   @RequestParam(defaultValue = CustomValue.pageSize) int pageSize) throws UnirestException, ParseException {
        return couponService.getCouponTarget(userDetails,couponNo,pageNumber,pageSize);
    }

}

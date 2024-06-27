package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.DeliveryLocationRequestDto;
import com.example.sampleroad.dto.response.DeliveryLocationResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.DeliveryLocationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Api(tags = {"배송지 관련 api Controller"})
public class DeliveryLocationController {

    private final DeliveryLocationService deliveryLocationService;

    @PostMapping("/api/delivery-location")
    @ApiOperation(value = "배송지 추가 api")
    public ResultInfo registerDeliveryLocation(@RequestBody DeliveryLocationRequestDto.CreateDeliveryLocation dto,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        DeliveryLocationResponseDto.DeliveryLocationRegister addressId = deliveryLocationService.registerDeliveryLocation(dto, userDetails);
        return new ResultInfo(ResultInfo.Code.CREATED, "배송지 추가 완료", addressId);
    }

    @GetMapping("/api/delivery-location")
    @ApiOperation(value = "배송지 목록 조회 api")
    public ResponseEntity<DeliveryLocationResponseDto.AllDeliveryLocations> findMemberAllDeliveryLocations(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        DeliveryLocationResponseDto.AllDeliveryLocations response = deliveryLocationService.findMemberAllDeliveryLocations(userDetails);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/delivery-location/{addressId}")
    @ApiOperation(value = "배송지 수정 api")
    public ResultInfo modifyDeliveryLocation(@PathVariable Long addressId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody DeliveryLocationRequestDto.Update dto) {
        deliveryLocationService.modifyDeliveryLocation(addressId, userDetails, dto);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "배송지 수정 완료");
    }

    @DeleteMapping("/api/delivery-location/{addressId}")
    @ApiOperation(value = "배송지 삭제 api")
    public ResultInfo removeDeliveryLocation(@PathVariable Long addressId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        deliveryLocationService.removeDeliveryLocation(addressId, userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "배송지 삭제 완료");
    }

}

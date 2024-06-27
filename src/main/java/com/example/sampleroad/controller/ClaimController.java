package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.ClaimRequestDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.ClaimService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"클레임 관련 api Controller"})
public class ClaimController {

    private final ClaimService claimService;

    // 커스텀 키트 환불 및 교환 가능하고 - 키트 전체에 대한 반품 및 교환만 가능합니다! (부분 불가)

    @PutMapping(value = "/api/claims/withdraw")
    @ApiOperation(value = "클레임 철회하기")
    public ResultInfo updateClaimWithdraw(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestBody ClaimRequestDto.UpdateClaimWithdraw claimWithdraw,
                                          @RequestParam(defaultValue = "") String orderNo) {
        claimService.updateClaimWithdraw(userDetails, claimWithdraw, orderNo);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "클레임 철회 성공");

    }

    @PostMapping(value = "/api/claims/return")
    @ApiOperation(value = "회원 반품 신청하기")
    public ResultInfo returnClaim(@RequestBody ClaimRequestDto.ReturnClaims dto,
                                  @AuthenticationPrincipal UserDetailsImpl userDetails,
                                  @RequestParam(defaultValue = "") String orderNo) {
        claimService.returnClaim(dto, userDetails, orderNo);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "반품 신청 성공");
    }

    @PostMapping(value = "/api/claims/exchange/{orderOptionNo}")
    @ApiOperation(value = "회원 교환 신청하기")
    public ResultInfo exchangeClaim(@RequestBody ClaimRequestDto.ExchangeClaims dto,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    @PathVariable String orderOptionNo,
                                    @RequestParam(defaultValue = "") String orderNo) {
        // TODO: 2023-05-24 커스텀 키트 - 교환 한번에 여러개 불가능이기 때문에 대책을 마련해야함
        // TODO: 2023-05-24 커스텀 키트 교환 | 관리자 키트 교환
        claimService.exchangeClaim(dto, userDetails, orderOptionNo, orderNo);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "교환 신청 성공");
    }
}

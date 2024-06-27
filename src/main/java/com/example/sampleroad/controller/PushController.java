package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.PushMessageRequestDto;
import com.example.sampleroad.dto.response.push.PushDataResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.PushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {" PUSH 알림 관련 api Controller"})
public class PushController {

    private final PushService pushService;

    @PostMapping("/api/push")
    @ApiOperation(value = "PUSH 발송 api")
    public ResultInfo sendPushNotify(@RequestBody PushMessageRequestDto.Send dto,
                                     @RequestParam(defaultValue = CustomValue.pushPageNumber) int pushPageNumber,
                                     @RequestParam(defaultValue = CustomValue.pushPageSize) int pushPageSize) {
        pushService.sendPushNotify(dto, pushPageNumber, pushPageSize);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "PUSH 발송 완료");
    }

    @PostMapping("/api/push/event")
    @ApiOperation(value = "PUSH 발송 api")
    public ResultInfo sendPushNotifyForEvent(@RequestBody PushMessageRequestDto.Send dto,
                                     @RequestParam(defaultValue = CustomValue.pushPageNumber) int pushPageNumber,
                                     @RequestParam(defaultValue = CustomValue.pushPageSize) int pushPageSize) {
        pushService.sendPushNotifyForEvent(dto, pushPageNumber, pushPageSize);
        //pushService.sendPushNotifyForEventIndividual(dto,pushPageNumber,pushPageSize);
        //pushService.sendCartProductNotifications(dto,pushPageNumber,pushPageSize);
        //pushService.sendPurchaseIn7DaysAgo(dto,pushPageNumber,pushPageSize);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "PUSH 발송 완료");
    }

    @GetMapping("/api/push/{pushId}")
    @ApiOperation(value = "PUSH 응답 발송 api")
    public PushDataResponseDto sendPushResponse(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long pushId) {
        return pushService.sendPushResponse(userDetails,pushId);
    }
}

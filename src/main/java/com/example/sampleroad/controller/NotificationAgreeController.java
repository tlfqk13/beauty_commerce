package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.PushNotifyRequestDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.NotificationAgreeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"푸시 알림 동의 api Controller"})
public class NotificationAgreeController {
    private final NotificationAgreeService notificationAgreeService;

    @PutMapping("/api/push-adnotify")
    @ApiOperation(value = "광고 알림 동의 수정 api")
    public ResultInfo modifyAdPushNotify(@RequestBody PushNotifyRequestDto pushNotifyRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificationAgreeService.modifyAdPushNotify(pushNotifyRequestDto, userDetails);

        return new ResultInfo(ResultInfo.Code.SUCCESS, "회원정보 수정완료");
    }

    @PutMapping("/api/push-smsnotify")
    @ApiOperation(value = "정보 알림 동의 수정 api")
    public ResultInfo modifyPushNotify(@RequestBody PushNotifyRequestDto pushNotifyRequestDto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificationAgreeService.modifySmsPushNotify(pushNotifyRequestDto, userDetails);

        return new ResultInfo(ResultInfo.Code.SUCCESS, "회원정보 수정완료");
    }

    @PutMapping("/api/push/hotdeal-notify")
    @ApiOperation(value = "주간 특가 알림 수정 api")
    public ResultInfo modifyWeeklyPriceNotify(@RequestParam(defaultValue = "true") boolean isNotify,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificationAgreeService.modifyWeeklyPriceNotify(isNotify, userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "주간특가 알림 설정 수정완료");
    }

    @PostMapping("/api/push/stock-notify/{productNo}")
    @ApiOperation(value = "재입고 알림 api")
    public ResultInfo registerStockNotify(@RequestParam(defaultValue = "true") boolean isNotify,
                                          @PathVariable int productNo,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificationAgreeService.registerStockNotify(isNotify, productNo, userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "재입고 알림 등록 완료");
    }

    @PutMapping("/api/push/stock-notify")
    @ApiOperation(value = "재입고 알림 전체 on/off api")
    public ResultInfo updateStockNotify(@RequestParam(defaultValue = "true") boolean isNotify,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificationAgreeService.updateStockNotify(isNotify, userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "재입고 알림 수정 완료");
    }
}

package com.example.sampleroad.controller;

import com.example.sampleroad.dto.response.splash.SplashResponseDto;
import com.example.sampleroad.service.SplashService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = {"스플래시 관련 api Controller"})
@Slf4j
@RequiredArgsConstructor
public class SplashController {

    private final SplashService splashService;

    @GetMapping("/api/splash")
    @ApiOperation("스플래시 조회 api")
    public SplashResponseDto getSplash() {
        return splashService.getSplash();
    }
}




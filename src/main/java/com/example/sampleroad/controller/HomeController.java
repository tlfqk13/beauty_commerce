package com.example.sampleroad.controller;

import com.example.sampleroad.dto.response.home.HomeResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.HomeService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"메인화면 api Controller"})
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/api/new-home")
    @ApiOperation(value = "홈 메인화면 조회 회원/비회원 api")
    public HomeResponseDto getNewHome(@AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        log.info("회원 홈 화면 요청");
        return homeService.getNewHome(userDetails);
    }
}

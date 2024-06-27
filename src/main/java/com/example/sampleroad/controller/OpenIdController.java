package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.AppleLoginRequestDto;
import com.example.sampleroad.dto.request.KakaoLoginRequestDto;
import com.example.sampleroad.dto.response.openId.OpenIdResponseDto;
import com.example.sampleroad.service.AppleIdService;
import com.example.sampleroad.service.OpenIdService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"SNS 간편 로그인 관련 api Controller"})
public class OpenIdController {

    private final OpenIdService openIdService;
    private final AppleIdService appleIdService;

    @GetMapping("/oauth/login-url")
    public ResultInfo getOpenIdUrl() throws UnirestException {
        OpenIdResponseDto openIdLoginUrl = openIdService.getOpenIdLoginUrl();
        return new ResultInfo(ResultInfo.Code.SUCCESS, "카카오 로그인 url 불러오기 완료", openIdLoginUrl);
    }

    @GetMapping("/oauth/callback")
    public ResultInfo getOpenIdAccessToken(@RequestParam String code) throws UnirestException, ParseException {
        HashMap<String, Object> openIdAccessToken = openIdService.getOpenIdAccessToken(code);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "카카오 로그인 완료", openIdAccessToken);
    }

    @PostMapping("/oauth/kakao-login")
    @ApiOperation(value = "카카오 로그인 api")
    public ResultInfo getOpenIdAccessToken(@RequestBody KakaoLoginRequestDto dto) throws UnirestException, ParseException {
        HashMap<String, Object> openIdAccessToken = openIdService.getOpenIdAccessToken(dto);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "카카오 로그인 완료", openIdAccessToken);
    }

    @GetMapping("/oauth/apple-login")
    public ResultInfo addMemberAppleLogin(@RequestParam String userIdentifier) throws UnirestException, ParseException {
        HashMap<String, Object> tokenInfo = appleIdService.addMemberAppleLogin(userIdentifier);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "애플 로그인 완료", tokenInfo);
    }

    @PostMapping("/oauth/apple-join")
    public ResultInfo addMemberAppleIdJoin(@RequestBody AppleLoginRequestDto.CreateMember createMember) throws UnirestException, ParseException {
        appleIdService.addMemberAppleIdJoin(createMember);
        return new ResultInfo(ResultInfo.Code.CREATED, "애플 회원가입 완료");
    }
}

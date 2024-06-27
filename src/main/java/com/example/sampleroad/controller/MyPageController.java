package com.example.sampleroad.controller;

import com.example.sampleroad.dto.response.member.MyPageResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.MyPageService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Api(tags = {"마이페이지 관련 api Controller"})
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/api/my-page")
    @ApiOperation(value = "마이페이지 상세정보 조회")
    public MyPageResponseDto getMemberInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        return myPageService.getMyPagePointAndCoupon(userDetails);
    }
}


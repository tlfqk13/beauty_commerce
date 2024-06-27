package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.MemberRequestDto;
import com.example.sampleroad.dto.request.TokenCheckRequestDto;
import com.example.sampleroad.dto.response.member.MemberResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.AuthenticationsService;
import com.example.sampleroad.service.PushService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"인증 관련 api Controller"})
public class AuthenticationController {

    private final AuthenticationsService authenticationsService;
    private final PushService pushService;

    @PostMapping(value = "/api/authentications/id-send")
    @ApiOperation(value = "인증번호 발송하기(아이디찾기)")
    public void sendAuthenticationNumberForId(@RequestBody MemberRequestDto.SendAuthenticationNumberById dto) throws UnirestException, ParseException {
        authenticationsService.sendAuthenticationNumberForId(dto);
    }

    @PostMapping(value = "/api/authentications/id-check")
    @ApiOperation(value = "인증 번호 대조 후 아이디 찾기")
    public ResponseEntity<MemberResponseDto.MemberFindId> findMemberIdAfterAuthenticationNumber(@RequestBody MemberRequestDto.FindMemberId dto) throws UnirestException, ParseException {
        MemberResponseDto.MemberFindId response = authenticationsService.findMemberIdAfterAuthenticationNumber(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/api/authentications/pw-send")
    @ApiOperation(value = "인증번호 발송하기(비밀번호찾기)")
    public void sendAuthenticationNumberForPw(@RequestBody MemberRequestDto.SendAuthenticationNumberByPw dto) throws UnirestException, ParseException {
        authenticationsService.sendAuthenticationNumberForPw(dto);
    }

    @PostMapping(value = "/api/authentications/pw-check")
    @ApiOperation(value = "인증 번호 확인 요청")
    public void findMemberIdAfterAuthenticationNumber(@RequestBody MemberRequestDto.FindMemberPw dto) throws UnirestException, ParseException {
        authenticationsService.findMemberPw(dto);
    }


    @PutMapping(value = "/api/authentications/pw")
    @ApiOperation(value = "인증 번호 대조 후 비밀번호 변경")
    public void updateMemberPw(@RequestBody MemberRequestDto.UpdateMemberPw dto) throws UnirestException, ParseException {
        authenticationsService.updateMemberPw(dto);
    }

    // TODO: 2023/11/14 업데이트 이전에 운영 반영하면 주석 처리
    @PostMapping(value = "/api/token-check")
    @ApiOperation(value = "토큰 유효성 검사")
    public ResultInfo CheckedMemberToken(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestBody TokenCheckRequestDto.PushToken dto) throws UnirestException, ParseException {
        HashMap<String, Object> resultInfo = authenticationsService.checkedMemberToken(userDetails);
        pushService.createPushToken(userDetails, dto.getPushToken());
        return new ResultInfo(ResultInfo.Code.SUCCESS, "토큰 유효성 검사 성공", resultInfo);
    }

    @GetMapping("/api/token-check")
    @ApiOperation(value = "토큰 유효성 검사")
    public ResultInfo CheckedMemberToken(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @RequestParam(defaultValue = "") String pushToken
    ) throws UnirestException, ParseException {
        HashMap<String, Object> resultInfo = authenticationsService.checkedMemberToken(userDetails);
        pushService.createPushToken(userDetails, pushToken);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "토큰 유효성 검사 성공", resultInfo);
    }

}

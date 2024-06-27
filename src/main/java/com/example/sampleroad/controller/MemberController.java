package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.ClientIdRequestDto;
import com.example.sampleroad.dto.request.MemberRequestDto;
import com.example.sampleroad.dto.response.member.MemberResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.MemberService;
import com.example.sampleroad.service.RefreshTokenService;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"사용자 관련 api Controller"})
public class MemberController {
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping(value = "/api/join")
    @ApiOperation(value = "회원가입 api")
    public ResponseEntity<ResultInfo> addMember(@RequestBody @Valid MemberRequestDto.CreateMember dto,
                                                BindingResult bindingResult) throws UnirestException, ParseException {
        validation(bindingResult);
        memberService.memberAdd(dto);
        ResultInfo resultInfo = new ResultInfo(ResultInfo.Code.CREATED, "회원가입 완료");
        return new ResponseEntity<>(resultInfo, HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/member")
    @ApiOperation(value = "회원정보 수정 api")
    public ResultInfo modifyMember(@RequestBody MemberRequestDto.UpdateMember dto,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        validation(bindingResult);
        memberService.memberModify(dto, userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "회원정보 수정완료");
    }

    @PutMapping("/api/member/change-password")
    @ApiOperation(value = "비밀번호 수정 api")
    public ResultInfo modifyMemberPassword(@RequestBody MemberRequestDto.ChangeMemberPw dto,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        memberService.modifyMemberPassword(dto,userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS,"비밀번호 변경완료");
    }

    /**
     * 샵바이 탈퇴 처리 + db 탈퇴상태 true 업데이트
     * 샵바이에서 탈퇴 철회시 db 탈퇴상태 직접 false로 처리해야함
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2023/07/07
     **/
    @DeleteMapping("/api/member")
    @ApiOperation(value = "회원탈퇴 api")
    public ResultInfo removeMember(@RequestBody MemberRequestDto.DeleteMember dto,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        memberService.memberRemove(dto, userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "회원탈퇴 완료");
    }

    @PostMapping("/api/login")
    @ApiOperation(value = "회원 로그인 api")
    public ResultInfo accessToken(@RequestBody MemberRequestDto.Login dto) throws UnirestException, ParseException {
        HashMap<String, Object> tokenInfo = memberService.loginMember(dto);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "로그인 완료", tokenInfo);
    }

    @PostMapping("/api/refresh")
    @ApiOperation(value = "엑세스 토큰 재발급")
    public ResultInfo refreshToken(@RequestBody MemberRequestDto.RefreshTokenUpdate dto) {

        HashMap<String, Object> tokenInfo = refreshTokenService.refreshToken(dto);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "엑세스 토큰 재발급 완료", tokenInfo);
    }

    @DeleteMapping("/api/logout")
    @ApiOperation(value = "회원 로그아웃 api")
    public ResultInfo logoutMember(@AuthenticationPrincipal UserDetailsImpl userDetails) throws UnirestException, ParseException {
        memberService.logoutMember(userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "회원 로그아웃 완료");
    }

    @GetMapping("/api/member")
    @ApiOperation(value = "회원 정보 조회")
    public ResultInfo getMemberInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        MemberResponseDto.MemberInfo memberInfo = memberService.getMemberInfo(userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "회원 정보 조회 완료", memberInfo);
    }

    @GetMapping("/api/member/ci-check")
    @ApiOperation(value = "ci 중복체크 api")
    public void CheckedCiDuplicate(@RequestParam String ci) {
        memberService.ciDuplicateChecked(ci);
    }

    @PostMapping("/api/member/ci-match")
    @ApiOperation(value = "ci 일치여부 확인 api")
    public ResultInfo CheckedCiMatch(@RequestBody ClientIdRequestDto.Match dto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.ciMatchChecked(dto.getCi(), userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "ci 일치여부 확인 성공");
    }

    @GetMapping("/api/member/login-id-check")
    @ApiOperation(value = "로그인 id 중복체크 api")
    public void CheckedMemberLoginIdDuplicate(@RequestParam String memberLoginId) {
        memberService.memberLoginIdDuplicateChecked(memberLoginId);
    }

    @GetMapping("/api/member/nickname-check")
    @ApiOperation(value = "닉네임 중복체크 api")
    public void CheckedNicknameDuplicate(@RequestParam String nickname) {
        memberService.nicknameDuplicateChecked(nickname);
    }

    @GetMapping("/api/member/email-check")
    @ApiOperation(value = "이메일 중복체크 api")
    public void CheckedEmailDuplicate(@RequestParam String email) {
        memberService.emailDuplicateChecked(email);
    }

    private void validation(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (FieldError fe : bindingResult.getFieldErrors()) {
                errorMap.put(fe.getField(), fe.getDefaultMessage());
            }
            throw new RuntimeException(errorMap.toString().replaceAll("[{}]", ""));
        }
    }
}

package com.example.sampleroad.service;

import com.example.sampleroad.dto.request.MemberRequestDto;
import com.example.sampleroad.dto.response.member.MemberResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.parser.ParseException;

import java.util.HashMap;

public interface AuthenticationsService {
    void sendAuthenticationNumberForId(MemberRequestDto.SendAuthenticationNumberById dto) throws UnirestException, ParseException;

    void sendAuthenticationNumberForPw(MemberRequestDto.SendAuthenticationNumberByPw dto) throws UnirestException, ParseException;

    MemberResponseDto.MemberFindId findMemberIdAfterAuthenticationNumber(MemberRequestDto.FindMemberId dto) throws UnirestException, ParseException;

    void updateMemberPw(MemberRequestDto.UpdateMemberPw dto) throws UnirestException, ParseException;

    HashMap<String, Object> checkedMemberToken(UserDetailsImpl userDetails) throws UnirestException, ParseException;

    void findMemberPw(MemberRequestDto.FindMemberPw dto) throws UnirestException, ParseException;

    void deleteAuthenticationSendCount();
}

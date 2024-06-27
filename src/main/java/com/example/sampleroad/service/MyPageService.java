package com.example.sampleroad.service;

import com.example.sampleroad.dto.response.member.MyPageResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.parser.ParseException;

public interface MyPageService {
    MyPageResponseDto getMyPagePointAndCoupon(UserDetailsImpl userDetails) throws UnirestException, ParseException;
}

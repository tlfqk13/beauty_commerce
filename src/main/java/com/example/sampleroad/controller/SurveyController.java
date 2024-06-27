package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.SurveyRequestDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.SurveyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Api(tags = {"피부 설문 관련 api Controller"})
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping("/api/survey")
    @ApiOperation(value = "설문 등록 api")
    public ResultInfo addSurvey(@RequestBody SurveyRequestDto.Create dto,
                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        surveyService.addSurvey(dto,userDetails);
        return new ResultInfo(ResultInfo.Code.CREATED, "설문 등록 완료");
    }

    @GetMapping("/api/survey")
    @ApiOperation(value = "설문 조회 api")
    public ResultInfo getAllSurvey(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return new ResultInfo(ResultInfo.Code.SUCCESS, "설문 조회 완료", surveyService.getSurvey(userDetails));
    }

    @PutMapping("/api/survey")
    @ApiOperation(value = "설문 수정 api")
    public ResultInfo modifySurvey(@RequestBody SurveyRequestDto.Create dto,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails){
        surveyService.modifySurvey(dto,userDetails);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "설문 수정 완료");
    }
}
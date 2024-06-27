package com.example.sampleroad.controller;

import com.example.sampleroad.common.utils.CustomValue;
import com.example.sampleroad.common.utils.ResultInfo;
import com.example.sampleroad.dto.request.zeroExperience.ZeroExperienceSurveyRequestDto;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceRecommendSurveyResponseDto;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceSurveyResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.service.ZeroExperienceReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Api(tags = {"샘플 서베이 리뷰 관련 api Controller"})
@RequestMapping("/api/zero-experience")
public class ZeroExperienceReviewController {

    private final ZeroExperienceReviewService zeroExperienceReviewService;

    @GetMapping("/items")
    @ApiOperation(value = "0원 체험 샘플 서베이 조회 api")
    public ZeroExperienceRecommendSurveyResponseDto getZeroExperienceItems(@RequestParam(defaultValue = "1") int pageNumber,
                                                                           @RequestParam(defaultValue = CustomValue.pageSize) int pageSize,
                                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return zeroExperienceReviewService.getZeroExperienceItems(pageNumber, pageSize, userDetails);

    }

    @PostMapping("recommend-survey/{ordersItemId}")
    @ApiOperation(value = "추천/비추천 설문 등록 api")
    public ResultInfo addRecommendSurvey(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @PathVariable Long ordersItemId,
                                         @RequestParam(defaultValue = "false") String isRecommend) {
        zeroExperienceReviewService.addRecommendSurvey(userDetails, ordersItemId, isRecommend);
        return new ResultInfo(ResultInfo.Code.CREATED, "설문 등록 완료");

    }

    @PutMapping("recommend-survey/{ordersItemId}")
    @ApiOperation(value = "추천/비추천 설문 수정 api")
    public ResultInfo modifyRecommendSurvey(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @PathVariable Long ordersItemId,
                                            @RequestParam(defaultValue = "false") String isRecommend) {
        zeroExperienceReviewService.modifyRecommendSurvey(userDetails, ordersItemId, isRecommend);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "설문 수정 완료");
    }

    @GetMapping("/question-survey/{ordersItemId}")
    @ApiOperation(value = "설문참여 누르면 나오는 설문 조회 api")
    public ZeroExperienceSurveyResponseDto getZeroExperienceQuestionSurvey(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                           @PathVariable Long ordersItemId) {
        return zeroExperienceReviewService.getZeroExperienceQuestionSurvey(userDetails, ordersItemId);

    }

    @PostMapping("/question-survey/{ordersItemId}")
    @ApiOperation(value = "질문 서베이 완료 후 등록 api")
    public ResultInfo addZeroExperienceQuestionSurvey(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @PathVariable Long ordersItemId,
                                                @RequestBody ZeroExperienceSurveyRequestDto requestDto) {
        zeroExperienceReviewService.addZeroExperienceQuestionSurvey(userDetails, ordersItemId, requestDto);
        return new ResultInfo(ResultInfo.Code.SUCCESS, "질문 서베이 등록 완료");

    }

}
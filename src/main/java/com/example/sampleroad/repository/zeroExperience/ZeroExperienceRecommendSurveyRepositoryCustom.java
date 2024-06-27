package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceRecommendSurveyQueryDto;

import java.util.List;

public interface ZeroExperienceRecommendSurveyRepositoryCustom {

    List<ZeroExperienceRecommendSurveyQueryDto> findByOrdersItemIds(List<Long> ordersItemIds, Long memberId);
    List<ZeroExperienceRecommendSurveyQueryDto> findByProductNo(int productNo);
}

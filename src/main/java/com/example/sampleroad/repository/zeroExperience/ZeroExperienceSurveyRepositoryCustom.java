package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceQuestionQueryDto;

import java.util.List;

public interface ZeroExperienceSurveyRepositoryCustom {
    List<ZeroExperienceQuestionQueryDto> findByIdWithQuestionsAndAnswers(Long questionSurveyId);
}

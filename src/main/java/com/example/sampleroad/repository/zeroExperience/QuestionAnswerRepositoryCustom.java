package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.domain.survey.QuestionAnswer;

import java.util.List;

public interface QuestionAnswerRepositoryCustom {

    List<QuestionAnswer> findQuestionAnswerListByAnswerIds(List<Long> answerIds);

}

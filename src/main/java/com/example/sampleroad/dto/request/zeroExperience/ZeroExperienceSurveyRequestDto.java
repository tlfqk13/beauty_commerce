package com.example.sampleroad.dto.request.zeroExperience;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
public class ZeroExperienceSurveyRequestDto {

    private Long questionSurveyId; // or String, depending on your ID format
    private List<QuestionAnswerDto> questionAnswers;
    private String textTypeAnswer;

    @NoArgsConstructor
    @Getter
    public static class QuestionAnswerDto {
        private String questionId; // or Long, depending on your ID format
        private List<Long> answerIds;

    }

    public List<Long> getAllAnswerIds() {
        return questionAnswers.stream()
                .flatMap(qaDto -> qaDto.answerIds.stream())
                .collect(Collectors.toList());
    }
}

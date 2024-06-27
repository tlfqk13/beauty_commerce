package com.example.sampleroad.dto.response.zeroExperienceReview;

import com.example.sampleroad.domain.survey.QuestionType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ZeroExperienceSurveyResponseDto {

    private Long questionSurveyId;
    private int productNo;
    private String productImgUrl;
    private String productName;
    private String brandName;
    private boolean isNecessary;
    private List<QuestionDto> questionList;


    public boolean getIsNecessary() {
        return isNecessary;
    }

    public ZeroExperienceSurveyResponseDto(Long questionSurveyId, int productNo, String productImgUrl,
                                           String productName, String brandName, boolean isNecessary,
                                           List<QuestionDto> questionList) {
        this.questionSurveyId = questionSurveyId;
        this.productNo = productNo;
        this.productImgUrl = productImgUrl;
        this.productName = productName;
        this.brandName = brandName;
        this.questionList = questionList;
    }

    @NoArgsConstructor
    @Getter
    public static class QuestionDto {
        private Long questionId;
        private String questionTitle;
        private QuestionType questionType;
        private Integer selectMaxCount;
        private List<QuestionAnswerDto> answerList;

        public QuestionDto(Long questionId, String questionTitle, QuestionType questionType,
                           Integer selectMaxCount,
                           List<QuestionAnswerDto> answerList) {
            this.questionId = questionId;
            this.questionTitle = questionTitle;
            this.questionType = questionType;
            this.selectMaxCount = selectMaxCount;
            this.answerList = answerList;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class QuestionAnswerDto {
        private Long answerId;
        private String optionText;
        private String selectedImageUrl;
        private String deSelectedImageUrl;

        public QuestionAnswerDto(Long answerId, String optionText) {
            this.answerId = answerId;
            this.optionText = optionText;
        }

        public QuestionAnswerDto(Long answerId, String optionText, String selectedImageUrl, String deSelectedImageUrl) {
            this.answerId = answerId;
            this.optionText = optionText;
            this.selectedImageUrl = selectedImageUrl;
            this.deSelectedImageUrl = deSelectedImageUrl;
        }
    }
}

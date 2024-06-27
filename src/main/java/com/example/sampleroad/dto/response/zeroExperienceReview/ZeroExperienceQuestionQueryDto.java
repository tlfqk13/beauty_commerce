package com.example.sampleroad.dto.response.zeroExperienceReview;

import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.domain.survey.QuestionType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ZeroExperienceQuestionQueryDto {

    private String questionContent;
    private QuestionType questionType;
    private Integer selectMaxCount;
    private String optionText;
    private Long questionId;
    private Long answerId;

    @QueryProjection
    public ZeroExperienceQuestionQueryDto(String questionContent, QuestionType questionType,
                                          Integer selectMaxCount,
                                          String optionText,
                                          Long questionId, Long answerId) {
        this.questionContent = questionContent;
        this.questionType = questionType;
        this.selectMaxCount = selectMaxCount;
        this.optionText = optionText;
        this.questionId = questionId;
        this.answerId = answerId;
    }

    @NoArgsConstructor
    @Getter
    public static class QuestionAnswerQueryDto {

        private String questionContent;
        private QuestionType questionType;
        private Long questionId;

        @QueryProjection
        public QuestionAnswerQueryDto(String questionContent, QuestionType questionType,
                                      Long questionId) {
            this.questionContent = questionContent;
            this.questionType = questionType;
            this.questionId = questionId;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class NecessaryOrdersItem {
        private Long memberId;
        private Long ordersItemId;
        private OrderStatus orderStatus;

        @QueryProjection
        public NecessaryOrdersItem(Long memberId, Long ordersItemId, OrderStatus orderStatus) {
            this.memberId = memberId;
            this.ordersItemId = ordersItemId;
            this.orderStatus = orderStatus;
        }
    }

}

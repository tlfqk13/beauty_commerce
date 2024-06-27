package com.example.sampleroad.domain.survey;

import com.example.sampleroad.common.utils.TimeStamped;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "QUESTION_ANSWER_MEMBER")
public class QuestionAnswerMember extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ZERO_EXPERIENCE_SURVEY_MEMBER_ID")
    private ZeroExperienceSurveyMember zeroExperienceSurveyMember;

    @Column(name = "TEXT_ANSWER")
    private String textAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ANSWER_ID")
    private QuestionAnswer questionAnswer;

    @Builder
    public QuestionAnswerMember(ZeroExperienceSurveyMember zeroExperienceSurveyMember, QuestionAnswer questionAnswer,
                                String textAnswer) {
        this.zeroExperienceSurveyMember = zeroExperienceSurveyMember;
        this.questionAnswer = questionAnswer;
        this.textAnswer = textAnswer;
    }
}

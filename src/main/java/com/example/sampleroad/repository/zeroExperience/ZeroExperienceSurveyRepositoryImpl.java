package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.dto.response.zeroExperienceReview.QZeroExperienceQuestionQueryDto;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceQuestionQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.survey.QQuestion.question;
import static com.example.sampleroad.domain.survey.QQuestionAnswer.questionAnswer;
import static com.example.sampleroad.domain.survey.QZeroExperienceSurvey.zeroExperienceSurvey;

public class ZeroExperienceSurveyRepositoryImpl implements ZeroExperienceSurveyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ZeroExperienceSurveyRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ZeroExperienceQuestionQueryDto> findByIdWithQuestionsAndAnswers(Long questionSurveyId) {
        return queryFactory
                .select(new QZeroExperienceQuestionQueryDto(
                        question.questionContent,
                        question.questionType,
                        question.selectMaxCount,
                        questionAnswer.optionText,
                        question.id,
                        questionAnswer.id
                ))
                .from(questionAnswer)
                .innerJoin(questionAnswer.question,question)
                .innerJoin(question.zeroExperienceSurvey,zeroExperienceSurvey)
                .where(zeroExperienceSurvey.id.eq(questionSurveyId))
                .fetch();

    }
}

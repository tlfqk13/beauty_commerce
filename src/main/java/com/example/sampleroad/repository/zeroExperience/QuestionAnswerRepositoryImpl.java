package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.domain.survey.QuestionAnswer;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.survey.QQuestion.question;
import static com.example.sampleroad.domain.survey.QQuestionAnswer.questionAnswer;

public class QuestionAnswerRepositoryImpl implements QuestionAnswerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public QuestionAnswerRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<QuestionAnswer> findQuestionAnswerListByAnswerIds(List<Long> answerIds) {

        return queryFactory.selectFrom(questionAnswer)
                .join(questionAnswer.question, question).fetchJoin()
                .where(questionAnswer.id.in(answerIds))
                .fetch();
    }
}

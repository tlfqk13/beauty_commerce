package com.example.sampleroad.repository.survey;

import com.example.sampleroad.dto.response.survey.QSurveyQueryDto_SurveyWithMember;
import com.example.sampleroad.dto.response.survey.SurveyQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.survey.QSurvey.survey;

public class SurveyRepositoryImpl implements SurveyRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public SurveyRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<SurveyQueryDto.SurveyWithMember> findSurveyWithMember(List<String> memberNos) {
        return queryFactory
                .select(new QSurveyQueryDto_SurveyWithMember(
                        member.id,
                        member.profileImageURL,
                        member.nickname,
                        survey.skinType,
                        member.memberNo,
                        survey.skinTrouble
                ))
                .from(member)
                .innerJoin(survey).on(member.id.eq(survey.member.id))
                .where(member.memberNo.in(memberNos))
                .fetch();
    }
}

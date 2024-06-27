package com.example.sampleroad.repository.member;

import com.example.sampleroad.dto.response.member.MemberQueryDto;
import com.example.sampleroad.dto.response.member.QMemberQueryDto_MemberInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.member.QMemberBank.memberBank;
import static com.example.sampleroad.domain.push.QNotificationAgree.notificationAgree;
import static com.example.sampleroad.domain.survey.QSurvey.survey;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {this.queryFactory = new JPAQueryFactory(em);}


    @Override
    public MemberQueryDto.MemberInfo findMemberInfo(Long memberId) {
        return queryFactory
                .select(new QMemberQueryDto_MemberInfo(
                        memberBank.bankName,
                        memberBank.bankAccount,
                        memberBank.bankDepositorName,
                        survey.skinTrouble,
                        survey.skinType,
                        survey.preference,
                        notificationAgree.smsAgreed,
                        notificationAgree.directMailAgreed,
                        notificationAgree.infoAdPushNotificationAgreed,
                        member.createdAt,
                        member.modifiedAt

                ))
                .from(member)
                .innerJoin(memberBank).on(member.id.eq(memberBank.member.id))
                .innerJoin(survey).on(member.id.eq(survey.member.id))
                .innerJoin(notificationAgree).on(member.id.eq(notificationAgree.member.id))
                .where(member.id.eq(memberId))
                .fetchOne();
    }

    @Override
    public boolean existsByMemberNo(String memberNo) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(member)
                .where(member.memberNo.eq(memberNo))
                .fetchFirst();

        return fetchFirst != null;
    }
}

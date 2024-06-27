package com.example.sampleroad.repository.authentication;

import com.example.sampleroad.dto.response.AuthenticationResponseDto;
import com.example.sampleroad.dto.response.QAuthenticationResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static com.example.sampleroad.domain.authentication.QAuthentication.authentication;
import static com.example.sampleroad.domain.member.QMember.member;

public class AuthenticationRepositoryImpl implements AuthenticationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public AuthenticationRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public AuthenticationResponseDto findByMobileNoAndMebmerName(String notiAccount, String memberName) {
        return queryFactory
                .select(new QAuthenticationResponseDto(
                        authentication.id,
                        member.memberName,
                        member.mobileNo,
                        member.memberLoginId,
                        authentication.sendCount
                ))
                .from(authentication)
                .innerJoin(authentication.member,member)
                .where(authentication.member.mobileNo.eq(notiAccount)
                        .and(authentication.member.memberName.eq(memberName))
                        .and(authentication.member.withdrawal.isFalse()))
                .fetchOne();

    }

    @Override
    public boolean existsByMobileNoAndMemberName(String notiAccount, String memberName) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(authentication)
                .innerJoin(authentication.member,member)
                .where(authentication.member.mobileNo.eq(notiAccount)
                        .and(authentication.member.memberName.eq(memberName))
                        .and(authentication.member.withdrawal.isFalse()))
                .fetchFirst();

        return fetchFirst != null;
    }
}

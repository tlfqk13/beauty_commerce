package com.example.sampleroad.repository.push;

import com.example.sampleroad.dto.response.push.PushResponseQueryDto;
import com.example.sampleroad.dto.response.push.QPushResponseQueryDto_AgreedMember;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.push.QPushToken.pushToken;

public class PushTokenRepositoryImpl implements PushTokenRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public PushTokenRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Set<PushResponseQueryDto.AgreedMember> findByMemberIds(List<Long> memberIds) {
        return new HashSet<>(queryFactory
                .select(new QPushResponseQueryDto_AgreedMember(
                        pushToken.member.id,
                        pushToken.token,
                        member.memberName
                ))
                .from(pushToken)
                .innerJoin(pushToken.member,member)
                .where(pushToken.member.id.in(memberIds))
                .fetch());
    }

    @Override
    public List<PushResponseQueryDto.AgreedMember> findByMemberIdsList(List<Long> memberIds) {
        return queryFactory
                .select(new QPushResponseQueryDto_AgreedMember(
                        pushToken.member.id,
                        pushToken.token,
                        member.memberName
                ))
                .from(pushToken)
                .innerJoin(pushToken.member,member)
                .where(pushToken.member.id.in(memberIds))
                .fetch();
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(pushToken)
                .innerJoin(pushToken.member,member)
                .where(pushToken.member.id.eq(memberId))
                .fetchFirst();

        return fetchFirst != null;
    }
}

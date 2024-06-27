package com.example.sampleroad.repository.delivery;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static com.example.sampleroad.domain.QDeliveryLocation.deliveryLocation;
import static com.example.sampleroad.domain.member.QMember.member;

public class DeliveryLocationImpl implements DeliveryLocationRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public DeliveryLocationImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(deliveryLocation)
                .innerJoin(deliveryLocation.member,member)
                .where(deliveryLocation.member.id.eq(memberId))
                .fetchFirst();

        return fetchFirst != null;
    }
}

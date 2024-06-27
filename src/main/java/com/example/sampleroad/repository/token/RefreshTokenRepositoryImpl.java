package com.example.sampleroad.repository.token;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static com.example.sampleroad.domain.QRefreshToken.refreshToken;
import static com.example.sampleroad.domain.QWishList.wishList;
import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.product.QProduct.product;

public class RefreshTokenRepositoryImpl implements RefreshTokenRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public RefreshTokenRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(refreshToken)
                .innerJoin(refreshToken.member, member)
                .where(refreshToken.member.id.eq(memberId))
                .fetchFirst();

        return fetchFirst != null;
    }
}

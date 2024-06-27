package com.example.sampleroad.repository.heart;

import com.example.sampleroad.dto.response.HeartQueryDto;
import com.example.sampleroad.dto.response.QHeartQueryDto_HeartWithReview;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.sampleroad.domain.QHeart.heart;
import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.product.QProduct.product;
import static com.example.sampleroad.domain.review.QReview.review;

public class HeartRepositoryImpl implements HeartRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public HeartRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<HeartQueryDto.HeartWithReview> findByMemberIdInAndReviewNoIn(Long memberId, List<Integer> reviewNos) {
        return queryFactory
                .select(new QHeartQueryDto_HeartWithReview(
                        member.id,
                        review.id,
                        heart.id,
                        review.reviewNo
                ))
                .from(heart)
                .innerJoin(heart.member,member)
                .innerJoin(heart.review,review)
                .where(member.id.in(memberId)
                        .and(review.reviewNo.in(reviewNos)))
                .fetch();
    }

    @Override
    public boolean existsByMemberIdAndReviewNo(Long memberId, int reviewNo) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(heart)
                .innerJoin(heart.member,member)
                .innerJoin(heart.review,review)
                .where(heart.member.id.eq(memberId)
                        .and(heart.review.reviewNo.eq(reviewNo)))
                .fetchFirst();

        return fetchFirst != null;
    }

    @Override
    public Set<Long> existsByMemberIdAndReviewNoIn(Long memberId, Set<Integer> reviewNos, int productNo) {
        List<Long> results = queryFactory
                .select(heart.review.id)
                .from(heart)
                .innerJoin(heart.member, member)
                .innerJoin(heart.review, review)
                .innerJoin(review.product, product)
                .where(heart.member.id.eq(memberId)
                        .and(heart.review.reviewNo.in(reviewNos))
                        .and(heart.review.product.productNo.eq(productNo)))
                .fetch();

        return new HashSet<>(results);
    }
}

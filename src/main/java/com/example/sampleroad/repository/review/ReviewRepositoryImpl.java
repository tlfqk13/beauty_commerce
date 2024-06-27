package com.example.sampleroad.repository.review;

import com.example.sampleroad.dto.response.review.QReviewQueryDto_ReviewInfo;
import com.example.sampleroad.dto.response.review.QReviewQueryDto_ReviewWithSurvey;
import com.example.sampleroad.dto.response.review.ReviewQueryDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.product.QProduct.product;
import static com.example.sampleroad.domain.review.QReview.review;
import static com.example.sampleroad.domain.survey.QSurvey.survey;

public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ReviewRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public ReviewQueryDto.ReviewWithSurvey findReviewWithSurvey(int reviewNo, String registerNo) {
        return queryFactory
                .select(new QReviewQueryDto_ReviewWithSurvey(
                        review.reviewNo,
                        member.id,
                        review.tag,
                        survey.skinTrouble,
                        member.profileImageURL,
                        member.nickname,
                        survey.skinType
                ))
                .from(member)
                .innerJoin(review).on(member.id.eq(review.member.id))
                .innerJoin(survey).on(member.id.eq(survey.member.id))
                .where(review.reviewNo.eq(reviewNo)
                        .and(member.memberNo.eq(registerNo)))
                .fetchOne();
    }

    @Override
    public Page<ReviewQueryDto.ReviewInfo> findReviewInfoByProductNo(Pageable pageable, int productNo) {
        List<ReviewQueryDto.ReviewInfo> content = queryFactory
                .select(new QReviewQueryDto_ReviewInfo(
                        review.id,
                        review.member.memberNo,
                        review.reviewNo,
                        product.id,
                        product.imgUrl,
                        product.brandName,
                        product.productNo,
                        review.createdAt.stringValue(),
                        review.content,
                        review.reviewRate,
                        review.recommendCount,
                        product.productName,
                        review.tag,
                        review.member.id,
                        review.member.nickname
                ))
                .from(review)
                .innerJoin(product).on(review.product.id.eq(product.id))
                .innerJoin(member).on(review.member.id.eq(member.id))
                .where(product.productNo.eq(productNo))
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(review.count())
                .from(review)
                .innerJoin(product).on(review.product.id.eq(product.id))
                .innerJoin(member).on(review.member.id.eq(member.id))
                .where(product.productNo.eq(productNo))
                .orderBy(review.createdAt.desc())
                .orderBy(product.productDiscountRate.desc())
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Double findReviewRateAvg(int productNo) {

        return queryFactory
                .select(review.reviewRate.avg())
                .from(review)
                .innerJoin(product).on(review.product.id.eq(product.id))
                .where(product.productNo.eq(productNo))
                .fetchOne();
    }

    @Override
    public boolean existsByReviewNo(int reviewNo) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(review)
                .where(review.reviewNo.eq(reviewNo))
                .fetchFirst();

        return fetchFirst != null;
    }

    @Override
    public Map<Integer, Integer> findReviewsInfoCount(Set<Integer> productNos) {
        List<Tuple> tuples = queryFactory
                .select(product.productNo, review.count())
                .from(review)
                .innerJoin(product).on(review.product.id.eq(product.id))
                .where(product.productNo.in(productNos))
                .groupBy(product.productNo)
                .fetch();

        return tuples.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(product.productNo),
                        tuple -> {
                            Long count = tuple.get(review.count());
                            return count != null ? Math.toIntExact(count) : 0;
                        }
                ));
    }

    @Override
    public ReviewQueryDto.ReviewInfo findReviewInfoByProductNo(int reviewNo, int productNo) {
        return queryFactory
                .select(new QReviewQueryDto_ReviewInfo(
                        review.id,
                        review.member.memberNo,
                        review.reviewNo,
                        product.id,
                        product.imgUrl,
                        product.brandName,
                        product.productNo,
                        review.createdAt.stringValue(),
                        review.content,
                        review.reviewRate,
                        review.recommendCount,
                        product.productName,
                        review.tag,
                        review.member.id,
                        review.member.nickname
                ))
                .from(review)
                .innerJoin(product).on(review.product.id.eq(product.id))
                .innerJoin(member).on(review.member.id.eq(member.id))
                .where(product.productNo.eq(productNo)
                        .and(review.reviewNo.in(reviewNo)))
                .fetchOne();
    }

    @Override
    public List<ReviewQueryDto.ReviewInfo> findByOrderOptionNosAndMemberId(List<Long> orderOptionNos, Long memberId) {
        return queryFactory
                .select(new QReviewQueryDto_ReviewInfo(
                        review.id,
                        review.member.memberNo,
                        review.reviewNo,
                        product.id,
                        product.imgUrl,
                        product.brandName,
                        product.productNo,
                        review.createdAt.stringValue(),
                        review.content,
                        review.reviewRate,
                        review.recommendCount,
                        product.productName,
                        review.tag,
                        review.member.id,
                        review.member.nickname
                ))
                .from(review)
                .innerJoin(product).on(review.product.id.eq(product.id))
                .innerJoin(member).on(review.member.id.eq(member.id))
                .where(review.orderOptionNo.in(orderOptionNos)
                        .and(review.member.id.eq(memberId)))
                .fetch();
    }

    @Override
    public Tuple findReviewCountAndRateAvg(int productNo) {

        return queryFactory
                .select(review.count().as("review_count"),
                        review.reviewRate.avg().as("average_review_rate"))
                .from(review)
                .innerJoin(review.product, product)
                .where(product.productNo.eq(productNo))
                .fetchOne();
    }

    @Override
    public boolean existsSampleRoadReviewByProductNo(int productNo) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(review)
                .innerJoin(review.product,product)
                .where(review.product.productNo.eq(productNo)
                        .and(review.isSampleRoadReview.isTrue()))
                .fetchFirst();
        return fetchFirst != null;

    }
}

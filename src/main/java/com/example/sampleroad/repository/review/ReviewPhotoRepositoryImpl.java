package com.example.sampleroad.repository.review;

import com.example.sampleroad.dto.response.review.QReviewPhotoQueryDto_ReviewPhoto;
import com.example.sampleroad.dto.response.review.ReviewPhotoQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.product.QProduct.product;
import static com.example.sampleroad.domain.review.QReview.review;
import static com.example.sampleroad.domain.review.QReviewPhoto.reviewPhoto;

public class ReviewPhotoRepositoryImpl implements ReviewPhotoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ReviewPhotoRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ReviewPhotoQueryDto.ReviewPhoto> findReviewPhotoByProductNo(int productNo, Pageable pageable) {
        List<ReviewPhotoQueryDto.ReviewPhoto> content = queryFactory
                .select(new QReviewPhotoQueryDto_ReviewPhoto(
                        reviewPhoto.id,
                        review.reviewNo,
                        product.productNo,
                        review.recommendCount,
                        reviewPhoto.reviewPhotoUrl
                ))
                .from(product)
                .innerJoin(review).on(product.id.eq(review.product.id))
                .innerJoin(reviewPhoto).on(review.id.eq(reviewPhoto.review.id))
                .where(product.productNo.eq(productNo))
                .orderBy(reviewPhoto.reviewPhotoUrl.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(product.count())
                .from(product)
                .innerJoin(review).on(product.id.eq(review.product.id))
                .innerJoin(reviewPhoto).on(review.id.eq(reviewPhoto.review.id))
                .where(product.productNo.eq(productNo))
                .orderBy(reviewPhoto.reviewPhotoUrl.asc())
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public List<ReviewPhotoQueryDto.ReviewPhoto> findReviewPhotoByProductNo(int productNo) {
        return queryFactory
                .select(new QReviewPhotoQueryDto_ReviewPhoto(
                        review.id,
                        review.reviewNo,
                        product.productNo,
                        review.recommendCount,
                        reviewPhoto.reviewPhotoUrl
                ))
                .from(product)
                .innerJoin(review).on(product.id.eq(review.product.id))
                .innerJoin(reviewPhoto).on(review.id.eq(reviewPhoto.review.id))
                .where(product.productNo.eq(productNo))
                .orderBy(reviewPhoto.reviewPhotoUrl.asc())
                .fetch();
    }

    @Override
    public List<ReviewPhotoQueryDto.ReviewPhoto> findReviewPhotoByReviewNo(int reviewNo, Long memberId) {
        List<ReviewPhotoQueryDto.ReviewPhoto> content = queryFactory
                .select(new QReviewPhotoQueryDto_ReviewPhoto(
                        reviewPhoto.id,
                        review.reviewNo,
                        product.productNo,
                        review.recommendCount,
                        reviewPhoto.reviewPhotoUrl
                ))
                .from(reviewPhoto)
                .innerJoin(reviewPhoto.review,review)
                .innerJoin(review.product,product)
                .innerJoin(review.member,member)
                .where(review.reviewNo.eq(reviewNo)
                        .and(review.member.id.eq(memberId)))
                .fetch();

        return content;
    }

    @Override
    public List<ReviewPhotoQueryDto.ReviewPhoto> findReviewPhotoByReviewNo(int reviewNo) {
        List<ReviewPhotoQueryDto.ReviewPhoto> content = queryFactory
                .select(new QReviewPhotoQueryDto_ReviewPhoto(
                        reviewPhoto.id,
                        review.reviewNo,
                        product.productNo,
                        review.recommendCount,
                        reviewPhoto.reviewPhotoUrl
                ))
                .from(reviewPhoto)
                .innerJoin(reviewPhoto.review,review)
                .innerJoin(review.product,product)
                .innerJoin(review.member,member)
                .where(review.reviewNo.eq(reviewNo))
                .fetch();

        return content;
    }
}

package com.example.sampleroad.repository.customkit;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.dto.response.customkit.CustomKitItemQueryDto;
import com.example.sampleroad.dto.response.customkit.QCustomKitItemQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.sampleroad.domain.QCategory.category;
import static com.example.sampleroad.domain.customkit.QCustomKit.customKit;
import static com.example.sampleroad.domain.customkit.QCustomKitItem.customKitItem;
import static com.example.sampleroad.domain.product.QProduct.product;

public class CustomKitRepositoryImpl implements CustomKitRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public CustomKitRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<CustomKitItemQueryDto> findCustomKitItem(boolean isOrdered, Long memberId) {
        return queryFactory
                .select(new QCustomKitItemQueryDto(
                        customKitItem.id,
                        customKit.id,
                        customKitItem.count,
                        customKitItem.productOptionNumber,
                        product.productNo,
                        customKit.orderNo
                ))
                .from(customKitItem)
                .innerJoin(customKitItem.product, product)
                .innerJoin(customKitItem.customKit, customKit)
                .where(customKit.isOrdered.eq(isOrdered)
                        .and(customKitItem.customKit.member.id.eq(memberId)))
                .fetch();


    }

    @Override
    public List<CustomKitItemQueryDto> findCustomKitItemByProductNos(boolean isOrdered, Long memberId, Set<Integer> productNos) {
        return queryFactory
                .select(new QCustomKitItemQueryDto(
                        customKitItem.id,
                        customKit.id,
                        customKitItem.count,
                        customKitItem.productOptionNumber,
                        product.productNo,
                        customKit.orderNo
                ))
                .from(customKitItem)
                .innerJoin(customKitItem.product, product)
                .innerJoin(customKitItem.customKit, customKit)
                .where(customKit.isOrdered.eq(isOrdered)
                        .and(customKitItem.customKit.member.id.eq(memberId))
                        .and(product.productNo.in(productNos)))
                .fetch();
    }

    @Override
    public List<CustomKitItemQueryDto> findCustomKitItemByProductNos(Long memberId, Set<Integer> productNos) {
        return queryFactory
                .select(new QCustomKitItemQueryDto(
                        customKitItem.id,
                        customKit.id,
                        customKitItem.count,
                        customKitItem.productOptionNumber,
                        product.productNo,
                        customKit.orderNo
                ))
                .from(customKitItem)
                .innerJoin(customKitItem.product, product)
                .innerJoin(customKitItem.customKit, customKit)
                .innerJoin(product.category,category)
                .where(customKitItem.customKit.member.id.eq(memberId)
                        .and(product.productNo.in(productNos))
                        .and(product.category.categoryDepth1.in(CategoryType.SAMPLE)))
                .fetch();
    }

    @Override
    public Optional<CustomKitItemQueryDto> findCustomKitItemAndProductNo(boolean isOrdered, Long memberId, int productNo) {
        return Optional.ofNullable(queryFactory
                .select(new QCustomKitItemQueryDto(
                        customKitItem.id,
                        customKit.id,
                        customKitItem.count,
                        customKitItem.productOptionNumber,
                        product.productNo,
                        customKit.orderNo
                ))
                .from(customKitItem)
                .innerJoin(customKitItem.product, product)
                .innerJoin(customKitItem.customKit, customKit)
                .where(customKit.isOrdered.eq(isOrdered)
                        .and(customKitItem.customKit.member.id.eq(memberId))
                        .and(product.productNo.in(productNo)))
                .fetchOne());

    }

    @Override
    public List<CustomKitItemQueryDto> findByOrderNoIn(Set<String> orderNos) {
        return queryFactory
                .select(new QCustomKitItemQueryDto(
                        customKitItem.id,
                        customKit.id,
                        customKitItem.count,
                        customKitItem.productOptionNumber,
                        product.productNo,
                        customKit.orderNo
                ))
                .from(customKitItem)
                .innerJoin(customKitItem.product, product)
                .innerJoin(customKitItem.customKit, customKit)
                .where(customKit.orderNo.in(orderNos))
                .orderBy(customKitItem.createdAt.desc())
                .fetch();
    }

    @Override
    public List<CustomKitItemQueryDto> findByOrderNoIn(String orderNo) {
        return queryFactory
                .select(new QCustomKitItemQueryDto(
                        customKitItem.id,
                        customKit.id,
                        customKitItem.count,
                        customKitItem.orderOptionNumber,
                        product.productNo,
                        customKit.orderNo
                ))
                .from(customKitItem)
                .innerJoin(customKitItem.product, product)
                .innerJoin(customKitItem.customKit, customKit)
                .where(customKit.orderNo.in(orderNo))
                .orderBy(customKitItem.orderOptionNumber.desc())
                .fetch();
    }

    @Override
    public List<CustomKitItemQueryDto> findByMemberIdAndProductNos(Long memberId, List<Integer> productNos) {
        return queryFactory
                .select(new QCustomKitItemQueryDto(
                        customKitItem.id,
                        customKit.id,
                        customKitItem.count,
                        customKitItem.productOptionNumber,
                        product.productNo,
                        customKit.orderNo
                ))
                .from(customKitItem)
                .innerJoin(customKitItem.product, product)
                .innerJoin(customKitItem.customKit, customKit)
                .where(customKit.isOrdered.eq(false)
                        .and(customKitItem.customKit.member.id.eq(memberId))
                        .and(product.productNo.in(productNos)))
                .fetch();

    }
}

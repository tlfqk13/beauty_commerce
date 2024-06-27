package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.dto.response.zeroExperienceReview.QZeroExperienceRecommendSurveyQueryDto;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceRecommendSurveyQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.order.QOrders.orders;
import static com.example.sampleroad.domain.order.QOrdersItem.ordersItem;
import static com.example.sampleroad.domain.product.QProduct.product;
import static com.example.sampleroad.domain.survey.QZeroExperienceRecommendSurvey.zeroExperienceRecommendSurvey;
import static com.example.sampleroad.domain.survey.QZeroExperienceSurveyMember.zeroExperienceSurveyMember;

public class ZeroExperienceRecommendSurveyRepositoryImpl implements ZeroExperienceRecommendSurveyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ZeroExperienceRecommendSurveyRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ZeroExperienceRecommendSurveyQueryDto> findByOrdersItemIds(List<Long> ordersItemIds, Long memberId) {
        return queryFactory
                .select(new QZeroExperienceRecommendSurveyQueryDto(
                        zeroExperienceRecommendSurvey.id,
                        ordersItem.id,
                        zeroExperienceSurveyMember.id,
                        zeroExperienceRecommendSurvey.isRecommend,
                        ordersItem.product.productNo,
                        ordersItem.product.productName,
                        ordersItem.product.brandName,
                        ordersItem.product.imgUrl,
                        ordersItem.orders.orderNo,
                        ordersItem.orders.createdAt
                ))
                .from(ordersItem)
                .innerJoin(ordersItem.product, product)
                .innerJoin(ordersItem.orders, orders)
                .innerJoin(orders.member, member)
                .innerJoin(zeroExperienceRecommendSurvey)
                .on(ordersItem.id.eq(zeroExperienceRecommendSurvey.ordersItem.id))
                .innerJoin(zeroExperienceSurveyMember)
                .on(orders.member.id.eq(zeroExperienceSurveyMember.member.id))
                .where(ordersItem.id.in(ordersItemIds).and(orders.member.id.eq(memberId)))
                .fetch();

    }

    @Override
    public List<ZeroExperienceRecommendSurveyQueryDto> findByProductNo(int productNo) {
        return queryFactory
                .select(new QZeroExperienceRecommendSurveyQueryDto(
                        zeroExperienceRecommendSurvey.id,
                        ordersItem.id,
                        zeroExperienceSurveyMember.id,
                        zeroExperienceRecommendSurvey.isRecommend,
                        ordersItem.product.productNo,
                        ordersItem.product.productName,
                        ordersItem.product.brandName,
                        ordersItem.product.imgUrl,
                        ordersItem.orders.orderNo,
                        ordersItem.orders.createdAt
                ))
                .from(ordersItem)
                .innerJoin(ordersItem.product, product)
                .innerJoin(ordersItem.orders, orders)
                .innerJoin(zeroExperienceRecommendSurvey)
                .on(ordersItem.id.eq(zeroExperienceRecommendSurvey.ordersItem.id))
                .innerJoin(zeroExperienceSurveyMember)
                .on(orders.member.id.eq(zeroExperienceSurveyMember.member.id))
                .where(ordersItem.product.productNo.eq(productNo))
                .fetch();
    }
}

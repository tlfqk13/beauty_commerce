package com.example.sampleroad.repository.product;

import com.example.sampleroad.domain.experience.ExperienceStatus;
import com.example.sampleroad.domain.product.EventProductType;
import com.example.sampleroad.dto.response.product.EventProductQueryDto;
import com.example.sampleroad.dto.response.product.QEventProductQueryDto_EventProductInfo;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import java.util.List;

import static com.example.sampleroad.domain.QCategory.category;
import static com.example.sampleroad.domain.experience.QExperience.experience;
import static com.example.sampleroad.domain.experience.QExperienceMember.experienceMember;
import static com.example.sampleroad.domain.product.QEventProduct.eventProduct;
import static com.example.sampleroad.domain.product.QProduct.product;

public class EventProductRepositoryImpl implements EventProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public EventProductRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<EventProductQueryDto.EventProductInfo> findEventProduct(List<Integer> productNoList) {
        return queryFactory.select(new QEventProductQueryDto_EventProductInfo(
                        eventProduct.eventTitle,
                        eventProduct.eventSubTitle,
                        eventProduct.eventName,
                        eventProduct.product.imgUrl,
                        eventProduct.eventPrice,
                        eventProduct.eventFinishTime,
                        eventProduct.product.productNo,
                        eventProduct.product.productName,
                        eventProduct.product.brandName,
                        eventProduct.product.category.categoryDepth1,
                        eventProduct.eventProductType
                ))
                .from(eventProduct)
                .innerJoin(eventProduct.product, product)
                .innerJoin(product.category, category)
                .where(eventProduct.isVisible.isTrue()
                        .and(eventProduct.product.productNo.in(productNoList)))
                .fetch();
    }

    @Override
    public List<EventProductQueryDto.EventProductInfo> findPriceZeroEventProduct(List<Integer> customKitProductOptionNos) {
        return queryFactory.select(new QEventProductQueryDto_EventProductInfo(
                        eventProduct.eventTitle,
                        eventProduct.eventSubTitle,
                        eventProduct.eventName,
                        eventProduct.product.imgUrl,
                        eventProduct.eventPrice,
                        eventProduct.eventFinishTime,
                        eventProduct.product.productNo,
                        eventProduct.product.productName,
                        eventProduct.product.brandName,
                        eventProduct.product.category.categoryDepth1,
                        eventProduct.eventProductType
                ))
                .from(eventProduct)
                .innerJoin(eventProduct.product, product)
                .innerJoin(product.category, category)
                .where(eventProduct.isVisible.isTrue()
                        .and(eventProduct.product.productOptionsNo.in(customKitProductOptionNos))
                        .and(eventProduct.eventPrice.eq(0)))
                .fetch();
    }

    @Override
    public List<EventProductQueryDto.EventProductInfo> findEventProductByIsVisible(EventProductType eventProductType) {

        BooleanExpression baseCondition = eventProduct.isVisible.isTrue();

        if (EventProductType.ALL.equals(eventProductType)) {
            baseCondition = baseCondition
                    .and(eventProduct.eventProductType.in(EventProductType.FIRST_DEAL, EventProductType.PURCHASE_CONDITION));
        } else {
            baseCondition = baseCondition
                    .and(eventProduct.eventProductType.in(eventProductType));
        }

        return queryFactory.select(new QEventProductQueryDto_EventProductInfo(
                        eventProduct.eventTitle,
                        eventProduct.eventSubTitle,
                        eventProduct.eventName,
                        eventProduct.product.imgUrl,
                        eventProduct.eventPrice,
                        eventProduct.eventFinishTime,
                        eventProduct.product.productNo,
                        eventProduct.product.productName,
                        eventProduct.product.brandName,
                        eventProduct.product.category.categoryDepth1,
                        eventProduct.eventProductType
                ))
                .from(eventProduct)
                .innerJoin(eventProduct.product, product)
                .innerJoin(product.category, category)
                .where(baseCondition)
                .fetch();
    }
}

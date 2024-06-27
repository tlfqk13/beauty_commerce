package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.dto.response.zeroExperienceReview.QZeroExperienceQuestionQueryDto_NecessaryOrdersItem;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceQuestionQueryDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.order.QOrders.orders;
import static com.example.sampleroad.domain.order.QOrdersItem.ordersItem;
import static com.example.sampleroad.domain.survey.QZeroExperienceQuestionItem.zeroExperienceQuestionItem;

public class ZeroExperienceQuestionItemRepositoryImpl implements ZeroExperienceQuestionItemRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public ZeroExperienceQuestionItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<ZeroExperienceQuestionQueryDto.NecessaryOrdersItem> findOrderItem(List<Long> ordersItemIds, Long memberId) {

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // Condition for OrderStatus.BUY_CONFIRM without the date condition
        BooleanExpression buyConfirmCondition = zeroExperienceQuestionItem.ordersItem.orders.orderStatus.eq(OrderStatus.BUY_CONFIRM);

        // Condition for OrderStatus.PAY_DONE with the date condition
        BooleanExpression payDoneAndBeforeSevenDaysAgo = zeroExperienceQuestionItem.ordersItem.orders.orderStatus.eq(OrderStatus.PAY_DONE)
                .and(zeroExperienceQuestionItem.createdAt.before(sevenDaysAgo));

        // Combine the two conditions with OR, then chain with AND for the memberId condition
        BooleanExpression finalCondition = buyConfirmCondition.or(payDoneAndBeforeSevenDaysAgo)
                .and(zeroExperienceQuestionItem.member.id.eq(memberId)
                        .and(zeroExperienceQuestionItem.ordersItem.id.in(ordersItemIds)));


        return queryFactory
                .select(new QZeroExperienceQuestionQueryDto_NecessaryOrdersItem(
                        ordersItem.orders.member.id,
                        ordersItem.id,
                        orders.orderStatus
                ))
                .from(zeroExperienceQuestionItem)
                .innerJoin(zeroExperienceQuestionItem.ordersItem, ordersItem)
                .innerJoin(zeroExperienceQuestionItem.ordersItem.orders,orders)
                .innerJoin(zeroExperienceQuestionItem.member, member)
                .where(finalCondition)
                .orderBy(zeroExperienceQuestionItem.createdAt.desc())
                .fetch();
    }

    @Override
    public boolean existsByMemberId(Long memberId) {

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // Condition for OrderStatus.BUY_CONFIRM without the date condition
        BooleanExpression buyConfirmCondition = zeroExperienceQuestionItem.ordersItem.orders.orderStatus.eq(OrderStatus.BUY_CONFIRM);

        // Condition for OrderStatus.PAY_DONE with the date condition
        BooleanExpression payDoneAndBeforeSevenDaysAgo = zeroExperienceQuestionItem.ordersItem.orders.orderStatus.eq(OrderStatus.PAY_DONE)
                .and(zeroExperienceQuestionItem.createdAt.before(sevenDaysAgo));

        // Combine the two conditions with OR, then chain with AND for the memberId condition
        BooleanExpression finalCondition = buyConfirmCondition.or(payDoneAndBeforeSevenDaysAgo)
                .and(zeroExperienceQuestionItem.member.id.eq(memberId));

        Integer fetchFirst = queryFactory
                .selectOne()
                .from(zeroExperienceQuestionItem)
                .innerJoin(zeroExperienceQuestionItem.member, member)
                .where(finalCondition)
                .fetchFirst();
        return fetchFirst != null;
    }
}

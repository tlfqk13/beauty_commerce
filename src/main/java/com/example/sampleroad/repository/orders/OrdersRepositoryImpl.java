package com.example.sampleroad.repository.orders;

import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.dto.response.order.OrdersQueryDto;
import com.example.sampleroad.dto.response.order.QOrdersQueryDto;
import com.example.sampleroad.dto.response.order.QOrdersQueryDto_OrderCntQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.order.QOrders.orders;
import static com.example.sampleroad.domain.order.QOrdersItem.ordersItem;

public class OrdersRepositoryImpl implements OrdersRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public OrdersRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<OrdersQueryDto> findByMemberIds(List<Long> memberIds) {
        return queryFactory
                .select(new QOrdersQueryDto(
                        orders.id,
                        orders.member.id
                ))
                .from(orders)
                .innerJoin(orders.member, member)
                .where(orders.member.id.in(memberIds))
                .groupBy(orders.member.id)
                .fetch();
    }

    @Override
    public boolean existsByMemberIdAndOrderStatus(Long memberId, List<OrderStatus> orderStatusList, LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(orders)
                .innerJoin(orders.member, member)
                .where(
                        orders.member.id.eq(memberId)
                                .and(orders.orderStatus.in(orderStatusList))
                                .and(orders.createdAt.between(eventStartDate, eventEndDate))
                )
                .fetchFirst();

        return fetchFirst != null;
    }

    @Override
    public List<OrdersQueryDto.OrderCntQueryDto> findOrderCntByOrderNos(List<String> orderNos, Long memberId) {
        return queryFactory
                .select(new QOrdersQueryDto_OrderCntQueryDto(
                        orders.id,
                        orders.orderNo,
                        ordersItem.productCnt
                ))
                .from(ordersItem)
                .innerJoin(ordersItem.orders, orders)
                .innerJoin(orders.member, member)
                .where(orders.orderNo.in(orderNos).and(orders.member.id.eq(memberId)))
                .fetch();
    }

    @Override
    public Page<OrdersQueryDto> findOrderIn7days(Pageable pageable) {

        LocalDate sevenDaysAgoDate = LocalDate.now().minusDays(9);
        LocalDateTime startOfSevenDaysAgo = sevenDaysAgoDate.atStartOfDay(); // Start of the day 7 days ago
        LocalDateTime endOfSevenDaysAgo = LocalDate.now().minusDays(2).atTime(LocalTime.MAX); // End of the day 7 days ago

        List<OrdersQueryDto> content = queryFactory
                .select(new QOrdersQueryDto(
                        orders.id,
                        orders.member.id
                ))
                .from(orders)
                .innerJoin(orders.member, member)
                .where(orders.modifiedAt
                        .between(startOfSevenDaysAgo, endOfSevenDaysAgo)
                        .and(orders.orderStatus.notIn(OrderStatus.CANCEL_DONE)))
                .groupBy(orders.member.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(orders.member.id.countDistinct())
                .from(orders)
                .innerJoin(orders.member, member)
                .where(orders.modifiedAt
                        .between(startOfSevenDaysAgo, endOfSevenDaysAgo)
                        .and(orders.orderStatus.notIn(OrderStatus.CANCEL_DONE)))
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }
}

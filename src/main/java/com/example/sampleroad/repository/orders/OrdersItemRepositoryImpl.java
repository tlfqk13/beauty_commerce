package com.example.sampleroad.repository.orders;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.dto.response.order.OrdersItemQueryDto;
import com.example.sampleroad.dto.response.order.QOrdersItemQueryDto;
import com.example.sampleroad.dto.response.order.QOrdersItemQueryDto_OrderIdByOrdersItem;
import com.example.sampleroad.dto.response.zeroExperienceReview.QZeroExperienceRecommendSurveyQueryDto_OrdersItemInfo;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceRecommendSurveyQueryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.example.sampleroad.domain.QCategory.category;
import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.order.QOrders.orders;
import static com.example.sampleroad.domain.order.QOrdersItem.ordersItem;
import static com.example.sampleroad.domain.product.QProduct.product;

public class OrdersItemRepositoryImpl implements OrdersItemRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public OrdersItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<OrdersItemQueryDto> findByOrderNoAndMemberIdAndOrderStatus(String orderNo, Long memberId, OrderStatus orderStatus) {
        return queryFactory
                .select(new QOrdersItemQueryDto(
                        ordersItem.id,
                        ordersItem.orderOptionNo,
                        ordersItem.product.id,
                        ordersItem.product.productNo,
                        ordersItem.product.productOptionsNo,
                        ordersItem.productCnt,
                        ordersItem.product.productName,
                        ordersItem.product.brandName,
                        ordersItem.product.imgUrl,
                        ordersItem.orders.orderNo,
                        ordersItem.product.category.categoryDepth1
                ))
                .from(ordersItem)
                .innerJoin(ordersItem.product, product)
                .innerJoin(ordersItem.orders, orders)
                .innerJoin(orders.member, member)
                .innerJoin(ordersItem.product.category, category)
                .where(orders.orderNo.eq(orderNo)
                        .and(orders.member.id.eq(memberId))
                        .and(ordersItem.orders.orderStatus.in(orderStatus))
                        .and(ordersItem.product.category.categoryDepth1.in((CategoryType.SAMPLE))))
                .fetch();
    }

    @Override
    public Boolean existsByOrderNo(String orderNo, Long memberId) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(ordersItem)
                .innerJoin(ordersItem.product, product)
                .innerJoin(ordersItem.orders, orders)
                .innerJoin(orders.member, member)
                .innerJoin(ordersItem.product.category, category)
                .where(orders.orderNo.eq(orderNo)
                        .and(orders.member.id.eq(memberId)))
                .fetchFirst();

        return fetchFirst != null;
    }

    @Override
    public OrdersItemQueryDto.OrderIdByOrdersItem findOrderIdByOrdersItemId(List<Integer> orderOptionNos, Long memberId) {
        return queryFactory
                .select(new QOrdersItemQueryDto_OrderIdByOrdersItem(
                        ordersItem.orders.id
                ))
                .from(ordersItem)
                .innerJoin(ordersItem.orders, orders)
                .innerJoin(orders.member, member)
                .where(ordersItem.orderOptionNo.in(orderOptionNos)
                        .and(orders.member.id.eq(memberId)))
                .fetchFirst(); // fetchOne() 대신 fetchFirst() 사용
    }

    @Override
    public List<OrdersItemQueryDto> findAllOrdersItem(Long ordersId, Long memberId) {
        return queryFactory
                .select(new QOrdersItemQueryDto(
                        ordersItem.id,
                        ordersItem.orderOptionNo,
                        ordersItem.product.id,
                        ordersItem.product.productNo,
                        ordersItem.product.productOptionsNo,
                        ordersItem.productCnt,
                        ordersItem.product.productName,
                        ordersItem.product.brandName,
                        ordersItem.product.imgUrl,
                        ordersItem.orders.orderNo,
                        ordersItem.product.category.categoryDepth1
                ))
                .from(ordersItem)
                .innerJoin(ordersItem.product, product)
                .innerJoin(ordersItem.orders, orders)
                .innerJoin(orders.member, member)
                .innerJoin(ordersItem.product.category, category)
                .where(orders.id.eq(ordersId)
                        .and(orders.member.id.eq(memberId)))
                .fetch();
    }

    @Override
    public Page<ZeroExperienceRecommendSurveyQueryDto.OrdersItemInfo> findOrdersItemByMemberIdAndKitCategory(Pageable pageable, Long memberId) {
        List<ZeroExperienceRecommendSurveyQueryDto.OrdersItemInfo> content = queryFactory
                .select(new QZeroExperienceRecommendSurveyQueryDto_OrdersItemInfo(
                        ordersItem.id,
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
                .innerJoin(ordersItem.product.category, category)
                // TODO: 4/5/24 팀구매도 kit 타입이라 샘플서베이만 빼와야함
                .where(orders.member.id.eq(memberId)
                        .and(ordersItem.product.category.categoryDepth2.in(CategoryType.EXPERIENCE))
                        .and(orders.orderStatus.in(OrderStatus.PAY_DONE,OrderStatus.BUY_CONFIRM)))
                .orderBy(orders.modifiedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(ordersItem.id.countDistinct())
                .from(ordersItem)
                .innerJoin(ordersItem.product, product)
                .innerJoin(ordersItem.orders, orders)
                .innerJoin(orders.member, member)
                .innerJoin(ordersItem.product.category, category)
                .where(orders.member.id.eq(memberId)
                        .and(ordersItem.product.category.categoryDepth2.in(CategoryType.EXPERIENCE))
                        .and(orders.orderStatus.in(OrderStatus.PAY_DONE,OrderStatus.BUY_CONFIRM)))
                .orderBy(orders.modifiedAt.desc())
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);

    }
}

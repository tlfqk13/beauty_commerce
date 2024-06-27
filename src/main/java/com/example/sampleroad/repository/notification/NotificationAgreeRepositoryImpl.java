package com.example.sampleroad.repository.notification;

import com.example.sampleroad.domain.SkinType;
import com.example.sampleroad.domain.cart.QCart;
import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.domain.push.PushType;
import com.example.sampleroad.dto.response.push.NotificationResponseQueryDto;
import com.example.sampleroad.dto.response.push.QNotificationResponseQueryDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.sampleroad.domain.cart.QCart.cart;
import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.order.QOrders.orders;
import static com.example.sampleroad.domain.push.QNotificationAgree.notificationAgree;
import static com.example.sampleroad.domain.survey.QSurvey.survey;

public class NotificationAgreeRepositoryImpl implements NotificationAgreeRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public NotificationAgreeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public NotificationResponseQueryDto findByMemberIdQueryDsl(Long memberId) {
        return queryFactory
                .select(new QNotificationResponseQueryDto(
                        notificationAgree.member.id,
                        notificationAgree.infoAdPushNotificationAgreed,
                        notificationAgree.isFirst
                ))
                .from(notificationAgree)
                .innerJoin(notificationAgree.member, member)
                .where(notificationAgree.member.id.eq(memberId))
                .fetchOne();
    }

    @Override
    public Page<NotificationResponseQueryDto> findByPushType(PushType pushType, Pageable pageable) {

        BooleanExpression condition = null;

        if (PushType.ADVERTISE.equals(pushType)) {
            condition = notificationAgree.infoAdPushNotificationAgreed.isTrue();
        } else {
            condition = notificationAgree.infoAdPushNotificationAgreed.isTrue();
        }

        List<NotificationResponseQueryDto> content = queryFactory
                .select(new QNotificationResponseQueryDto(
                        notificationAgree.member.id,
                        notificationAgree.infoAdPushNotificationAgreed,
                        notificationAgree.isFirst
                ))
                .from(notificationAgree)
                .innerJoin(notificationAgree.member, member)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(notificationAgree.count())
                .from(notificationAgree)
                .innerJoin(notificationAgree.member, member)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }
    @Override
    public Page<NotificationResponseQueryDto> findByPushTypeAndMemberIds(PushType pushType, Pageable pageable, List<Long> memberIds) {

        BooleanExpression condition = null;

        if (PushType.ADVERTISE.equals(pushType)) {
            condition = notificationAgree.infoAdPushNotificationAgreed.isTrue();
        } else {
            condition = notificationAgree.infoAdPushNotificationAgreed.isTrue();
        }

        List<NotificationResponseQueryDto> content = queryFactory
                .select(new QNotificationResponseQueryDto(
                        notificationAgree.member.id,
                        notificationAgree.infoAdPushNotificationAgreed,
                        notificationAgree.isFirst
                ))
                .from(notificationAgree)
                .innerJoin(notificationAgree.member, member)
                .where(condition.and(notificationAgree.member.id.in(memberIds)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(notificationAgree.count())
                .from(notificationAgree)
                .innerJoin(notificationAgree.member, member)
                .where(condition.and(notificationAgree.member.id.in(memberIds)))
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(notificationAgree)
                .innerJoin(notificationAgree.member, member)
                .where(notificationAgree.member.id.eq(memberId))
                .fetchFirst();

        return fetchFirst != null;
    }

    @Override
    public boolean existsByMemberIdAndIsFirst(Long memberId, boolean isFirst) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(notificationAgree)
                .innerJoin(notificationAgree.member, member)
                .where(notificationAgree.member.id.eq(memberId)
                        .and(notificationAgree.isFirst.eq(isFirst)))
                .fetchFirst();

        return fetchFirst != null;
    }

    @Override
    public Page<NotificationResponseQueryDto> findWithoutPurchaseHistory(Pageable pageable) {

        List<NotificationResponseQueryDto> content = queryFactory
                .select(new QNotificationResponseQueryDto(
                        notificationAgree.member.id,
                        notificationAgree.infoAdPushNotificationAgreed,
                        notificationAgree.isFirst
                ))
                .from(notificationAgree)
                .leftJoin(orders).on(notificationAgree.member.id.eq(orders.member.id))
                .where(orders.member.isNull()
                        .and(notificationAgree.infoAdPushNotificationAgreed.isTrue())
                        .and(orders.orderStatus.in(OrderStatus.PAY_DONE)))  // orders 테이블에 해당 member가 없는 경우만 선택
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(notificationAgree.count())
                .from(notificationAgree)
                .leftJoin(orders).on(notificationAgree.member.id.eq(orders.member.id))
                .where(orders.member.isNull()
                        .and(notificationAgree.infoAdPushNotificationAgreed.isTrue())
                        .and(orders.orderStatus.in(OrderStatus.PAY_DONE)))  // orders 테이블에 해당 member가 없는 경우만 선택
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<NotificationResponseQueryDto> findHasCart(Pageable pageable) {
        // 현재 시간으로부터 2주 전을 계산
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);

        // 최신 modifiedAt을 가진 cart 정보에 대한 서브쿼리 준비
        QCart cartSub = new QCart("cartSub");
        // 최신 cart 정보에 해당하는 memberId 조회
        List<Long> latestCartMemberIds = queryFactory
                .select(cart.member.id)
                .from(cart)
                .where(cart.modifiedAt.eq(
                        JPAExpressions.select(cartSub.modifiedAt.max())
                                .from(cartSub)
                                .where(cartSub.member.id.eq(cart.member.id)
                                        .and(cartSub.modifiedAt.between(twoWeeksAgo, LocalDateTime.now())))
                ))
                .groupBy(cart.member.id)
                .fetch();

        // 조회된 memberId를 기준으로 메인 쿼리 실행
        List<NotificationResponseQueryDto> content = queryFactory
                .select(new QNotificationResponseQueryDto(
                        notificationAgree.member.id,
                        notificationAgree.infoAdPushNotificationAgreed,
                        notificationAgree.isFirst
                ))
                .from(notificationAgree)
                .where(notificationAgree.member.id.in(latestCartMemberIds)
                        .and(notificationAgree.infoAdPushNotificationAgreed.isTrue()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 계산을 위한 쿼리는 중복 제거 로직 반영 필요
        Long totalCount = queryFactory
                .select(notificationAgree.count())
                .from(notificationAgree)
                .where(notificationAgree.member.id.in(latestCartMemberIds)
                        .and(notificationAgree.infoAdPushNotificationAgreed.isTrue()))
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }

    @Override
    public Page<NotificationResponseQueryDto> findBySkinCondition(Pageable pageable, SkinType skinType) {
        List<NotificationResponseQueryDto> content = queryFactory
                .select(new QNotificationResponseQueryDto(
                        notificationAgree.member.id,
                        notificationAgree.infoAdPushNotificationAgreed,
                        notificationAgree.isFirst
                ))
                .from(notificationAgree)
                .innerJoin(notificationAgree.member,member)
                .innerJoin(survey).on(survey.member.id.eq(member.id))
                .where(notificationAgree.infoAdPushNotificationAgreed.isTrue()
                        .and(survey.skinType.in("건성"))
                        .or(survey.skinTrouble.contains("속건조").or(survey.skinTrouble.contains("민감함")))
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(notificationAgree.count())
                .from(notificationAgree)
                .innerJoin(notificationAgree.member,member)
                .innerJoin(survey).on(survey.member.id.eq(member.id))
                .where(notificationAgree.infoAdPushNotificationAgreed.isTrue()
                        .and(survey.skinType.in("건성"))
                        .and(survey.skinTrouble.contains("속건조")))
                .fetchOne();

        return new PageImpl<>(content, pageable, totalCount);
    }
}

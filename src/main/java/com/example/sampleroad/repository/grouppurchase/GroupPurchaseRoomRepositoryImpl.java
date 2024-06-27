package com.example.sampleroad.repository.grouppurchase;

import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoom;
import com.example.sampleroad.domain.grouppurchase.QGroupPurchaseRoom;
import com.example.sampleroad.domain.grouppurchase.QGroupPurchaseRoomMember;
import com.example.sampleroad.domain.product.QProduct;
import com.example.sampleroad.dto.response.grouppurchase.GroupPurchaseQueryDto;
import com.example.sampleroad.dto.response.grouppurchase.QGroupPurchaseQueryDto;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.sampleroad.domain.grouppurchase.QGroupPurchaseRoom.groupPurchaseRoom;
import static com.example.sampleroad.domain.grouppurchase.QGroupPurchaseRoomMember.groupPurchaseRoomMember;
import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.product.QProduct.product;

public class GroupPurchaseRoomRepositoryImpl implements GroupPurchaseRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public GroupPurchaseRoomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<GroupPurchaseRoom> findEmptyRoomByMemberId(int productNo, Long memberId) {
        QGroupPurchaseRoom qGroupPurchaseRoom = QGroupPurchaseRoom.groupPurchaseRoom;
        QGroupPurchaseRoomMember qGroupPurchaseRoomMember = QGroupPurchaseRoomMember.groupPurchaseRoomMember;
        QProduct qProduct = QProduct.product;

        return queryFactory
                .selectFrom(qGroupPurchaseRoom)
                .join(qGroupPurchaseRoom.product, qProduct)
                .where(qProduct.productNo.eq(productNo))
                .where(qGroupPurchaseRoom.isFull.isFalse())
                .where(qGroupPurchaseRoom.deadLine.after(LocalDateTime.now())) // 현재 시간 이후의 deadline을 가진 방
                .where(qGroupPurchaseRoom.id.notIn( // excludedMemberId를 제외하는 조건
                        JPAExpressions
                                .select(qGroupPurchaseRoomMember.groupPurchaseRoom.id)
                                .from(qGroupPurchaseRoomMember)
                                .where(qGroupPurchaseRoomMember.member.id.eq(memberId))
                ))
                .where(qGroupPurchaseRoom.id.in( // is_payment_finish가 true인 멤버만 포함하는 조건
                        JPAExpressions
                                .select(qGroupPurchaseRoomMember.groupPurchaseRoom.id)
                                .from(qGroupPurchaseRoomMember)
                                .where(qGroupPurchaseRoomMember.isPaymentFinish.isTrue().and(qGroupPurchaseRoomMember.orders.id.isNotNull()))
                ))
                .fetch();
    }

    @Override
    public List<GroupPurchaseRoom> findEmptyRoom(int productNo) {
        QGroupPurchaseRoom qGroupPurchaseRoom = QGroupPurchaseRoom.groupPurchaseRoom;
        QGroupPurchaseRoomMember qGroupPurchaseRoomMember = QGroupPurchaseRoomMember.groupPurchaseRoomMember;
        QProduct qProduct = QProduct.product;

        return queryFactory
                .selectFrom(qGroupPurchaseRoom)
                .join(qGroupPurchaseRoom.product, qProduct)
                .where(qProduct.productNo.eq(productNo))
                .where(qGroupPurchaseRoom.isFull.isFalse())
                .where(qGroupPurchaseRoom.deadLine.after(LocalDateTime.now())) // 현재 시간 이후의 deadline을 가진 방
                .where(qGroupPurchaseRoom.id.in( // is_payment_finish가 true인 멤버만 포함하는 조건
                        JPAExpressions
                                .select(qGroupPurchaseRoomMember.groupPurchaseRoom.id)
                                .from(qGroupPurchaseRoomMember)
                                .where(qGroupPurchaseRoomMember.isPaymentFinish.isTrue().and(qGroupPurchaseRoomMember.orders.id.isNotNull()))
                ))
                .fetch();
    }

    @Override
    public List<GroupPurchaseRoom> findGroupPurchaseRoom(int productNo) {
        return queryFactory
                .selectFrom(groupPurchaseRoom)
                .from(groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .where(product.productNo.eq(productNo)
                        .and(groupPurchaseRoom.isFull.isFalse()
                                .and(groupPurchaseRoom.deadLine.after(LocalDateTime.now())))
                        .and(groupPurchaseRoom.id.in(
                                JPAExpressions
                                        .select(groupPurchaseRoomMember.groupPurchaseRoom.id)
                                        .from(groupPurchaseRoomMember)
                                        .where(groupPurchaseRoomMember.isPaymentFinish.isTrue())
                        )))
                .orderBy(groupPurchaseRoom.deadLine.asc())
                .fetch();
    }

    @Override
    public List<GroupPurchaseQueryDto> findByRoomId(Long roomId) {
        return queryFactory
                .select(new QGroupPurchaseQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoomMember.member.profileImageURL,
                        groupPurchaseRoomMember.memberRoomType,
                        groupPurchaseRoomMember.member.id,
                        groupPurchaseRoom.isFull
                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .innerJoin(groupPurchaseRoomMember.member, member)
                .where(groupPurchaseRoom.id.eq(roomId)
                        .and(groupPurchaseRoomMember.isPaymentFinish.isTrue()))
                .fetch();
    }
}

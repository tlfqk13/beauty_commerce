package com.example.sampleroad.repository.grouppurchase;

import com.example.sampleroad.domain.grouppurchase.QGroupPurchaseRoomMember;
import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.dto.response.grouppurchase.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.sampleroad.domain.grouppurchase.QGroupPurchaseRoom.groupPurchaseRoom;
import static com.example.sampleroad.domain.grouppurchase.QGroupPurchaseRoomMember.groupPurchaseRoomMember;
import static com.example.sampleroad.domain.member.QMember.member;
import static com.example.sampleroad.domain.order.QOrders.orders;
import static com.example.sampleroad.domain.product.QProduct.product;

public class GroupPurchaseRoomMemberRepositoryImpl implements GroupPurchaseRoomMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public GroupPurchaseRoomMemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<GroupPurchaseQueryDto> findLastOneProduct() {

        // Define an alias for groupPurchaseRoomMember for the subquery
        QGroupPurchaseRoomMember subGroupPurchaseRoomMember = new QGroupPurchaseRoomMember("subGroupPurchaseRoomMember");

        List<GroupPurchaseQueryDto> results = queryFactory
                .select(new QGroupPurchaseQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoom.product.imgUrl,
                        groupPurchaseRoomMember.memberRoomType,
                        groupPurchaseRoomMember.id,
                        groupPurchaseRoom.isFull
                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .where(groupPurchaseRoomMember.createdAt.in(
                        JPAExpressions
                                .select(subGroupPurchaseRoomMember.createdAt.max())
                                .from(subGroupPurchaseRoomMember)
                                .where(subGroupPurchaseRoomMember.groupPurchaseRoom.product.productNo.eq(groupPurchaseRoom.product.productNo)
                                        .and(subGroupPurchaseRoomMember.isPaymentFinish.isTrue()))
                                .groupBy(subGroupPurchaseRoomMember.groupPurchaseRoom.product.productNo)
                ).and(groupPurchaseRoom.deadLine.after(LocalDateTime.now())))
                .groupBy(groupPurchaseRoom.product.productNo, groupPurchaseRoom.product.productName, groupPurchaseRoom.deadLine)
                .fetch();

        return results;
    }

    @Override
    public List<GroupPurchaseQueryDto> findIsFullByRoomId(Long roomId) {
        return queryFactory
                .select(new QGroupPurchaseQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoom.product.imgUrl,
                        groupPurchaseRoomMember.memberRoomType,
                        groupPurchaseRoomMember.id,
                        groupPurchaseRoom.isFull

                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .where(groupPurchaseRoom.id.in(roomId)
                        .and(groupPurchaseRoomMember.isPaymentFinish.isTrue()))
                .fetch();
    }

    @Override
    public List<GroupPurchaseQueryDto.MemberProfileQueryDto> findByProductNos(List<Integer> productNos) {
        return queryFactory
                .select(new QGroupPurchaseQueryDto_MemberProfileQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoom.id,
                        groupPurchaseRoomMember.member.profileImageURL,
                        groupPurchaseRoomMember.member.nickname,
                        groupPurchaseRoomMember.member.id,
                        groupPurchaseRoom.isFull
                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .innerJoin(groupPurchaseRoomMember.member, member)
                .where(product.productNo.in(productNos)
                        .and(groupPurchaseRoomMember.isPaymentFinish.isTrue()))
                .fetch();
    }

    @Override
    public List<GroupPurchaseQueryDto.MemberProfileQueryDto> findGroupPurchaseRoomMembers(int productNo) {
        return queryFactory
                .select(new QGroupPurchaseQueryDto_MemberProfileQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoom.id,
                        groupPurchaseRoomMember.member.profileImageURL,
                        groupPurchaseRoomMember.member.nickname,
                        groupPurchaseRoomMember.member.id,
                        groupPurchaseRoom.isFull
                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .innerJoin(groupPurchaseRoomMember.member, member)
                .where(product.productNo.in(productNo)
                        .and(groupPurchaseRoomMember.isPaymentFinish.isTrue()))
                .fetch();
    }

    @Override
    public List<GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto> findGroupPurchaseRoomMembers(List<Long> roomIds) {
        return queryFactory
                .select(new QGroupPurchaseQueryDto_GroupPurchaseOrderInfoQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoomMember.member.id,
                        groupPurchaseRoomMember.orders.orderNo,
                        groupPurchaseRoom.id,
                        groupPurchaseRoom.roomCapacity,
                        groupPurchaseRoom.product.imgUrl
                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .innerJoin(groupPurchaseRoomMember.member, member)
                .innerJoin(groupPurchaseRoomMember.orders,orders)
                .where(groupPurchaseRoomMember.isPaymentFinish.isTrue()
                        .and(groupPurchaseRoomMember.groupPurchaseRoom.id.in(roomIds))
                        .and(groupPurchaseRoomMember.orders.orderStatus.notIn(OrderStatus.CANCEL_DONE))
                        .and(groupPurchaseRoom.deadLine.after(LocalDateTime.now())))
                .fetch();
    }

    @Override
    public Optional<GroupPurchaseQueryDto.MemberProfileQueryDto> findPaymentFinishGroupPurchaseRoomMembersByOrderNo(String orderNo) {
        return Optional.ofNullable(queryFactory
                .select(new QGroupPurchaseQueryDto_MemberProfileQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoom.id,
                        groupPurchaseRoomMember.member.profileImageURL,
                        groupPurchaseRoomMember.member.nickname,
                        groupPurchaseRoomMember.member.id,
                        groupPurchaseRoom.isFull
                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .innerJoin(groupPurchaseRoomMember.member, member)
                .innerJoin(groupPurchaseRoomMember.orders, orders)
                .where(groupPurchaseRoomMember.isPaymentFinish.isTrue()
                        .and(groupPurchaseRoomMember.orders.orderNo.eq(orderNo)))
                .fetchOne());
    }

    @Override
    public Optional<GroupPurchaseQueryDto.MemberProfileQueryDto> findGroupPurchaseRoomMembersAllByOrderNo(String orderNo) {
        return Optional.ofNullable(queryFactory
                .select(new QGroupPurchaseQueryDto_MemberProfileQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoom.id,
                        groupPurchaseRoomMember.member.profileImageURL,
                        groupPurchaseRoomMember.member.nickname,
                        groupPurchaseRoomMember.member.id,
                        groupPurchaseRoom.isFull
                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .innerJoin(groupPurchaseRoomMember.member, member)
                .innerJoin(groupPurchaseRoomMember.orders, orders)
                .where(groupPurchaseRoomMember.orders.orderNo.eq(orderNo))
                .fetchOne());
    }

    @Override
    public List<GroupPurchaseQueryDto.GroupPurchaseOrderQueryDto> findPaymentFinishGroupPurchaseRoomMembersByOrderNos(List<String> orderNos) {
        return queryFactory
                .select(new QGroupPurchaseQueryDto_GroupPurchaseOrderQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoom.id,
                        groupPurchaseRoom.isFull,
                        groupPurchaseRoomMember.orders.orderNo
                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .innerJoin(groupPurchaseRoomMember.member, member)
                .innerJoin(groupPurchaseRoomMember.orders, orders)
                .where(groupPurchaseRoomMember.orders.orderNo.in(orderNos))
                .fetch();
    }

    @Override
    public List<GroupPurchaseQueryDto.GroupPurchaseOrderQueryDto> findGroupPurchaseRoomMembersAllByOrderNos(List<String> orderNos) {
        return queryFactory
                .select(new QGroupPurchaseQueryDto_GroupPurchaseOrderQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoom.id,
                        groupPurchaseRoom.isFull,
                        groupPurchaseRoomMember.orders.orderNo
                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .innerJoin(groupPurchaseRoomMember.member, member)
                .innerJoin(groupPurchaseRoomMember.orders, orders)
                .where(groupPurchaseRoomMember.orders.orderNo.in(orderNos))
                .fetch();
    }

    /**
     * 팀구매현황 조회 쿼리
     *
     * @param
     * @return
     * @author sondong-gyu
     * @version 1.0.0
     * @date 2/29/24
     **/
    @Override
    public List<GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto> findGroupPurchaseRoomMembers(Long memberId) {
        return  queryFactory
                .select(new QGroupPurchaseQueryDto_GroupPurchaseOrderInfoQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoomMember.member.id,
                        groupPurchaseRoomMember.orders.orderNo,
                        groupPurchaseRoom.id,
                        groupPurchaseRoom.roomCapacity,
                        groupPurchaseRoom.product.imgUrl
                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .innerJoin(groupPurchaseRoomMember.member, member)
                .innerJoin(groupPurchaseRoomMember.orders, orders)
                .where(groupPurchaseRoomMember.orders.id.isNotNull()
                        .and(groupPurchaseRoomMember.orders.orderStatus.notIn(OrderStatus.CANCEL_DONE))
                        .and(groupPurchaseRoomMember.member.id.eq(memberId))
                        .and(groupPurchaseRoom.deadLine.after(LocalDateTime.now())))
                .fetch();
    }

    @Override
    public Optional<GroupPurchaseQueryDto.MemberProfileQueryDto> findCancelGroupPurchaseRoomMembersByOrderNo(String orderNo) {
        return Optional.ofNullable(queryFactory
                .select(new QGroupPurchaseQueryDto_MemberProfileQueryDto(
                        groupPurchaseRoom.product.productNo,
                        groupPurchaseRoom.product.productName,
                        groupPurchaseRoom.deadLine.stringValue(),
                        groupPurchaseRoom.id,
                        groupPurchaseRoomMember.member.profileImageURL,
                        groupPurchaseRoomMember.member.nickname,
                        groupPurchaseRoomMember.member.id,
                        groupPurchaseRoom.isFull
                ))
                .from(groupPurchaseRoomMember)
                .innerJoin(groupPurchaseRoomMember.groupPurchaseRoom, groupPurchaseRoom)
                .innerJoin(groupPurchaseRoom.product, product)
                .innerJoin(groupPurchaseRoomMember.member, member)
                .innerJoin(groupPurchaseRoomMember.orders, orders)
                .where(groupPurchaseRoomMember.orders.orderNo.eq(orderNo)
                        .and(groupPurchaseRoomMember.orders.orderStatus.in(OrderStatus.CANCEL_DONE)))
                .fetchOne());
    }
}

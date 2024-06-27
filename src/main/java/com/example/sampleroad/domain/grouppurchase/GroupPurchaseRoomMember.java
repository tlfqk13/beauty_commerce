package com.example.sampleroad.domain.grouppurchase;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.order.Orders;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "GROUP_PURCHASE_ROOM_MEMBER")
public class GroupPurchaseRoomMember extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GROUP_PURCHASE_ROOM_MEMBER_ID")
    private Long id;

    @Column(name = "IS_PAYMENT_FINISH")
    private boolean isPaymentFinish;

    @Column(name = "MEMBER_ROOM_TYPE")
    @Enumerated(EnumType.STRING)
    private GroupPurchaseType memberRoomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_PURCHASE_ROOM_ID")
    private GroupPurchaseRoom groupPurchaseRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Orders orders;

    public boolean getIsPaymentFinish() {
        return isPaymentFinish;
    }

    @Builder
    public GroupPurchaseRoomMember(GroupPurchaseRoom groupPurchaseRoom, Member member, GroupPurchaseType memberRoomType) {
        this.groupPurchaseRoom = groupPurchaseRoom;
        this.isPaymentFinish = false;
        this.member = member;
        this.memberRoomType = memberRoomType;
    }

    public void updatePaymentFinish(boolean isPaymentFinish) {
        this.isPaymentFinish = isPaymentFinish;
    }

    public void updateOrderId(Orders orders) {
        this.orders = orders;
    }
}

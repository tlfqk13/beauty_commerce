package com.example.sampleroad.domain.order;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ORDERS")
public class Orders extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDERS_ID")
    private Long id;

    @Column(name = "ORDER_NO")
    private String orderNo;

    // orders item 만들었는지 여부
    @Column(name = "IS_MADE_ORDERSITEM")
    private boolean isMadeOrdersItem;

    @Column(name = "ORDER_STATUS")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public Orders(String orderNo, Member member, boolean isMadeOrdersItem) {
        this.orderNo = orderNo;
        this.member = member;
        this.orderStatus = OrderStatus.PAY_DONE;
        this.isMadeOrdersItem = isMadeOrdersItem;
    }

    public boolean getIsMadeOrdersItem() {
        return isMadeOrdersItem;
    }

    public void updateIsMadeOrdersItem(boolean isMadeOrdersItem) {
        this.isMadeOrdersItem = isMadeOrdersItem;
    }

    public void updateOrderStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }
}

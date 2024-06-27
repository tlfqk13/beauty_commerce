package com.example.sampleroad.domain.cart;

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
@Table(name = "CART")
public class Cart extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CART_ID")
    private Long id;

    @Column(name = "CART_NO")
    private int cartNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Column(name = "ORDER_SHEET_NO")
    private String orderSheetNo;

    @Column(name = "IS_CUSTOMKIT")
    private boolean isCustomKit;

    @Builder
    private Cart(Member member, int cartNo, boolean isCustomKit) {
        this.member = member;
        this.cartNo = cartNo;
        this.isCustomKit = isCustomKit;
    }

    public void updateOrderSheetNo(String orderSheetNo) {
        this.orderSheetNo = orderSheetNo;
    }

}

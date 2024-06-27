package com.example.sampleroad.domain.survey;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.order.OrdersItem;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ZERO_EXPERIENCE_QUESTION_ITEM")
public class ZeroExperienceQuestionItem extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ZERO_EXPERIENCE_QUESTION_ITEM_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDERS_ITEM_ID")
    private OrdersItem ordersItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;



    @Builder
    public ZeroExperienceQuestionItem(OrdersItem ordersItem, Member member, LocalDateTime createAt) {
        this.ordersItem = ordersItem;
        this.member = member;
        this.createdAt = createAt;
    }
}

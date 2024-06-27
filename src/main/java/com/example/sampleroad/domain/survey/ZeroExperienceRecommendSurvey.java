package com.example.sampleroad.domain.survey;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.order.OrdersItem;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ZERO_EXPERIENCE_RECOMMEND_SURVEY")
public class ZeroExperienceRecommendSurvey extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ZERO_EXPERIENCE_RECOMMEND_SURVEY_ID")
    private Long id;

    @Column(name = "IS_RECOMMEND")
    private Boolean isRecommend;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDERS_ITEM_ID")
    private OrdersItem ordersItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public ZeroExperienceRecommendSurvey(Boolean isRecommend, OrdersItem ordersItem, Member member) {
        this.isRecommend = isRecommend;
        this.ordersItem = ordersItem;
        this.member = member;
    }

    public Boolean getIsRecommend() {
        return isRecommend;
    }

    public void updateIsRecommend(boolean isRecommend) {
        this.isRecommend = isRecommend;
    }
}

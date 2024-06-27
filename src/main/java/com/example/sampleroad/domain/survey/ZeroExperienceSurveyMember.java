package com.example.sampleroad.domain.survey;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.order.OrdersItem;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ZERO_EXPERIENCE_SURVEY_MEMBER")
public class ZeroExperienceSurveyMember extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ZERO_EXPERIENCE_SURVEY_MEMBER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @OneToMany(mappedBy = "zeroExperienceSurveyMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionAnswerMember> questionAnswers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ZERO_EXPERIENCE_SURVEY_ID")
    private ZeroExperienceSurvey zeroExperienceSurvey;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDERS_ITEM_ID")
    private OrdersItem ordersItem;

    @Builder
    public ZeroExperienceSurveyMember(Member member, ZeroExperienceSurvey zeroExperienceSurvey, OrdersItem ordersItem) {
        this.member = member;
        this.zeroExperienceSurvey = zeroExperienceSurvey;
        this.ordersItem = ordersItem;
    }
}

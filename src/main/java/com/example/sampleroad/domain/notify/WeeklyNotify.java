package com.example.sampleroad.domain.notify;

import com.example.sampleroad.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "WEEKLY_NOTIFY")
public class WeeklyNotify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WEEKLY_NOTIFY_ID")
    private Long id;

    @Column(name = "WEEKLY_NOTIFICATION_AGREE")
    private Boolean weeklyNotificationAgree;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public WeeklyNotify(Boolean weeklyNotificationAgree, Member member) {
        this.weeklyNotificationAgree = weeklyNotificationAgree;
        this.member = member;
    }

    public void updateNotifyStatus(boolean isNotify) {
        this.weeklyNotificationAgree = isNotify;
    }
}

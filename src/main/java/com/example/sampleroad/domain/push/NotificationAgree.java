package com.example.sampleroad.domain.push;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.dto.request.PushNotifyRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "NOTIFICATION_AGREE")
public class NotificationAgree extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTIFICATION_ID")
    private Long id;

    @Column(name = "INFO_AD_NOTIFICATION_AGREED")
    private Boolean infoAdPushNotificationAgreed;

    @Column(name = "IS_FIRST")
    private Boolean isFirst;

    @Column(name = "SMS_AGREED")
    private Boolean smsAgreed;

    @Column(name = "DIRECT_MAIL_AGREED")
    private Boolean directMailAgreed;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public NotificationAgree(Boolean isFirst, Boolean smsAgreed, Boolean directMailAgreed,
                             Member member) {
        this.isFirst = isFirst;
        this.smsAgreed = smsAgreed;
        this.directMailAgreed = directMailAgreed;
        this.member = member;
    }

    public void updateNotificationAgree(PushNotifyRequestDto pushNotifyRequestDto) {
        if (pushNotifyRequestDto.getInfoAdPushNotificationAgreed() != null) {
            this.infoAdPushNotificationAgreed = pushNotifyRequestDto.getInfoAdPushNotificationAgreed();
        }
        if (pushNotifyRequestDto.getInfoSmsEmailNotificationAgreed() != null) {
            this.directMailAgreed = pushNotifyRequestDto.getInfoSmsEmailNotificationAgreed();
            this.smsAgreed = pushNotifyRequestDto.getInfoSmsEmailNotificationAgreed();
        }
        this.isFirst = false;
    }

    public void updateNotificationAgree(boolean isNotify) {
        this.infoAdPushNotificationAgreed = isNotify;
    }
}

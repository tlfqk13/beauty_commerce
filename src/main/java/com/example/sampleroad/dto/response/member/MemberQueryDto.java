package com.example.sampleroad.dto.response.member;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class MemberQueryDto {

    @NoArgsConstructor
    @Getter
    @Setter
    public static class MemberInfo {
        private String refundBank;
        private String refundBankAccount;
        private String refundBankDepositorName;
        private String skinTrouble;
        private String skinType;
        private String preference;
        private Boolean smsAgree;
        private Boolean directMailAgreed;
        private Boolean infoAdPushNotificationAgreed;
        private Boolean hotDealPushNotificationAgreed;
        private Boolean restockNotificationAgreed;
        private LocalDateTime createTime;
        private LocalDateTime modifyTime;

        @QueryProjection
        public MemberInfo(String refundBank, String refundBankAccount, String refundBankDepositorName, String skinTrouble,
                          String skinType, String preference,Boolean smsAgree, Boolean directMailAgreed,
                          Boolean infoAdPushNotificationAgreed,
                          LocalDateTime createTime, LocalDateTime modifyTime) {
            this.refundBank = refundBank;
            this.refundBankAccount = refundBankAccount;
            this.refundBankDepositorName = refundBankDepositorName;
            this.skinTrouble = skinTrouble;
            this.skinType = skinType;
            this.preference = preference;
            this.smsAgree = smsAgree;
            this.directMailAgreed = directMailAgreed;
            this.infoAdPushNotificationAgreed = infoAdPushNotificationAgreed;
            this.createTime = createTime;
            this.modifyTime = modifyTime;
        }
    }
}

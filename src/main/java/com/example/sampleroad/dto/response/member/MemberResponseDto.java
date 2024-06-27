package com.example.sampleroad.dto.response.member;

import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.member.RegisterType;
import com.example.sampleroad.dto.response.DeliveryLocationResponseDto;
import com.example.sampleroad.dto.response.survey.SurveyResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberResponseDto {
    // TODO: 2023/11/14 memberNo 추가
    private String memberNo;
    private String memberName;
    private String nickname;
    private String birthday;
    private String mobileNo;
    private String memberLoginId;
    private String sex;
    private String email;
    private String refundBank;
    private String refundBankAccount;
    private String refundBankDepositorName;
    private String profileImageURL;
    private RegisterType registerType;
    private Boolean smsAgreed;
    private Boolean directMailAgreed;
    private Boolean infoAdPushNotificationAgreed;
    private Boolean hotDealPushNotificationAgreed;
    private Boolean restockNotificationAgreed;

    public MemberResponseDto(MemberQueryDto.MemberInfo memberInfo, Member member) {
        this.memberNo = member.getMemberNo();
        this.registerType = member.getRegisterType();
        this.memberName = member.getMemberName();
        this.nickname = member.getNickname();
        this.birthday = member.getBirthday();
        this.mobileNo = member.getMobileNo();
        this.memberLoginId = member.getMemberLoginId();
        this.sex = member.getSex();
        this.email = member.getEmail();
        this.refundBank = memberInfo.getRefundBank();
        this.refundBankAccount = memberInfo.getRefundBankAccount();
        this.refundBankDepositorName = memberInfo.getRefundBankDepositorName();
        this.profileImageURL = member.getProfileImageURL();
        this.smsAgreed = memberInfo.getSmsAgree();
        this.directMailAgreed = memberInfo.getDirectMailAgreed();
        this.infoAdPushNotificationAgreed = memberInfo.getInfoAdPushNotificationAgreed();
        this.hotDealPushNotificationAgreed = memberInfo.getHotDealPushNotificationAgreed();
        this.restockNotificationAgreed = memberInfo.getRestockNotificationAgreed();
    }

    @NoArgsConstructor
    @Getter
    public static class MemberInfo {
        private Boolean isNewMember;
        private MemberResponseDto memberInfo;
        private SurveyResponseDto survey;
        private DeliveryLocationResponseDto.DeliveryLocation deliveryLocation;

        public MemberInfo(Boolean isNewMember, MemberResponseDto memberResponseDto, SurveyResponseDto surveyResponseDto,
                          DeliveryLocationResponseDto.DeliveryLocation deliveryLocation) {
            this.isNewMember = isNewMember;
            this.memberInfo = memberResponseDto;
            this.survey = surveyResponseDto;
            this.deliveryLocation = deliveryLocation;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class MemberFindId {
        private String userLoginId;

        public MemberFindId(String userLoginId) {
            this.userLoginId = userLoginId;
        }
    }
}

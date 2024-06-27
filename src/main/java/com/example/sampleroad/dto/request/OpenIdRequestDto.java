package com.example.sampleroad.dto.request;

import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.member.RegisterType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OpenIdRequestDto {

    @NoArgsConstructor
    @Getter
    public static class CreateOpenIdMember {
        private String mobileNo;
        private String memberName;
        private String birthday;
        private String sex;
        private String email;
        private String nickName;
        private boolean pushNotificationAgreed;
        private String[] joinTermsAgreements;
        private Boolean smsAgreed;
        private Boolean directMailAgreed;

        public CreateOpenIdMember(String mobileNo, String memberName, String birthday, String sex
                , String email, String nickName, boolean pushNotificationAgreed
                , String[] joinTermsAgreements, Boolean smsAgreed, Boolean directMailAgreed) {
            this.mobileNo = mobileNo;
            this.memberName = memberName;
            this.birthday = birthday;
            this.sex = sex;
            this.email = email;
            this.nickName = nickName;
            this.pushNotificationAgreed = pushNotificationAgreed;
            this.joinTermsAgreements = joinTermsAgreements;
            this.smsAgreed = smsAgreed;
            this.directMailAgreed = directMailAgreed;
        }
    }


    @NoArgsConstructor
    @Getter
    public static class CreateMember {
        private String nickname;
        private boolean pushNotificationAgreed;
        private String[] joinTermsAgreements;
        private String mobileNo;
        private String memberName;
        private String ci;
        private String birthday;
        private String memberId;
        private String password;
        private String sex;
        private String memberNo;
        private String email;
        private Boolean smsAgreed;
        private Boolean directMailAgreed;
        private String memberStatus;

        public CreateMember(String nickname, boolean pushNotificationAgreed,
                            String[] joinTermsAgreements, String mobileNo, String memberName, String ci,
                            String birthday, String memberId, String password, String sex,
                            String memberNo, String email, Boolean smsAgreed, Boolean directMailAgreed,
                            String memberStatus) {
            this.nickname = nickname;
            this.pushNotificationAgreed = pushNotificationAgreed;
            this.joinTermsAgreements = joinTermsAgreements;
            this.mobileNo = mobileNo;
            this.memberName = memberName;
            this.ci = ci;
            this.birthday = birthday;
            this.memberId = memberId;
            this.password = password;
            this.sex = sex;
            this.memberNo = memberNo;
            this.email = email;
            this.smsAgreed = smsAgreed;
            this.directMailAgreed = directMailAgreed;
            this.memberStatus = memberStatus;
        }

        public CreateMember(String memberNo) {
            this.memberNo = memberNo;
        }


        public Member toEntity(String accessToken, String nickname) {
            return Member.builder()
                    .nickname(nickname)
                    .email(email)
                    .pushNotificationAgreed(pushNotificationAgreed)
                    .joinTermsAgreements("")
                    .mobileNo(mobileNo)
                    .memberName(memberName)
                    .ci(ci)
                    .birthday(birthday)
                    .memberLoginId(memberId)
                    .password(password)
                    .sex(sex)
                    .memberNo(memberNo)
                    .registerType(RegisterType.KAKAO)
                    .shopByAccessToken(accessToken)
                    .build();
        }
    }
}

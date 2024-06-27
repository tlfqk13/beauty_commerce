package com.example.sampleroad.dto.request;

import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.member.RegisterType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AppleLoginRequestDto {
    @NoArgsConstructor
    @Getter
    public static class CreateMember {
        private String nickname;

        private boolean pushNotificationAgreed;

        private boolean smsAgreed;

        private boolean directMailAgreed;

        private String[] joinTermsAgreements;

        private String mobileNo;

        private String memberName;

        private String ci;

        private String birthday;

        private String userIdentifier;
        private String sex;
        private String memberId;
        private String password;

        public CreateMember(AppleLoginRequestDto.CreateMember createMember, String appleMemberId, String password) {
            this.nickname = createMember.getNickname();
            this.pushNotificationAgreed = true;
            this.joinTermsAgreements = createMember.getJoinTermsAgreements();
            this.mobileNo = createMember.getMobileNo();
            this.memberName = createMember.getMemberName();
            this.ci = createMember.getCi();
            this.birthday = createMember.getBirthday();
            this.userIdentifier = createMember.getUserIdentifier();
            this.sex = createMember.getSex();
            this.memberId = appleMemberId;
            this.password = password;
        }

        public Member toEntity(String joinTermsAgreements, String memberNo, String appleMemberId, String encode) {
            return Member.builder()
                    .nickname(nickname)
                    .pushNotificationAgreed(pushNotificationAgreed)
                    .joinTermsAgreements(joinTermsAgreements)
                    .mobileNo(mobileNo)
                    .memberName(memberName)
                    .memberLoginId(appleMemberId)
                    .password(encode)
                    .ci(ci)
                    .birthday(birthday)
                    .sex(sex)
                    .memberNo(memberNo)
                    .registerType(RegisterType.APPLE)
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class Login {
        private String memberLoginId;
        private String password;

        public Login(String memberLoginId, String password) {
            this.memberLoginId = memberLoginId;
            this.password = password;
        }
    }
}

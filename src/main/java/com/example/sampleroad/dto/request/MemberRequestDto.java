package com.example.sampleroad.dto.request;

import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.member.RegisterType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@Getter
public class MemberRequestDto {

    @NoArgsConstructor
    @Getter
    public static class CreateMember {
        @NotBlank(message = "닉네임을 입력 해주세요")
        private String nickname;

        private boolean pushNotificationAgreed;

        private boolean smsAgreed;

        private boolean directMailAgreed;

        private String[] joinTermsAgreements;

        private String mobileNo;

        private String memberName;

        private String ci;

        private String birthday;

        private String memberId;

        private String password;

        private String sex;

        public Member toEntity(String joinTermsAgreements, String password, String memberNo) {
            return Member.builder()
                    .nickname(nickname)
                    .joinTermsAgreements(joinTermsAgreements)
                    .mobileNo(mobileNo)
                    .memberName(memberName)
                    .ci(ci)
                    .birthday(birthday)
                    .memberLoginId(memberId)
                    .password(password)
                    .sex(sex)
                    .memberNo(memberNo)
                    .registerType(RegisterType.APP)
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class UpdateMember {
        private String email;

        private String nickname;

        private String memberName;

        private String birthday;

        private String mobileNo;

        private String password;

        private String refundBank;

        private String refundBankAccount;

        private String refundBankDepositorName;

        private String sex;

        private String profileImageURL;

        private Boolean pushNotificationAgreed;

        private Boolean smsAgreed;

        private Boolean directMailAgreed;

        private String[] skinType;

        private String[] skinTrouble;

        private String[] preference;
    }

    @NoArgsConstructor
    @Getter
    public static class DeleteMember {
        private String reason;
    }

    @NoArgsConstructor
    @Getter
    public static class Login {
        private String memberLoginId;
        private String password;
    }

    @NoArgsConstructor
    @Getter
    public static class RefreshTokenUpdate {
        private String memberLoginId;
        private String password;
        private String refreshToken;
    }

    @NoArgsConstructor
    @Getter
    public static class SendAuthenticationNumberById {
        private String notiAccount; // phoneNumber
        private String memberName;
    }

    @NoArgsConstructor
    @Getter
    public static class SendAuthenticationNumberByPw {
        private String userLoginId;
        private String notiAccount;
    }

    @NoArgsConstructor
    @Getter
    public static class FindMemberId {
        private String phoneNumber;
        private String memberName;
        private String certificatedNumber;
    }

    @NoArgsConstructor
    @Getter
    public static class FindMemberPw {
        private String userLoginId;
        private String phoneNumber;
        private String certificatedNumber;
    }

    @NoArgsConstructor
    @Getter
    public static class UpdateMemberPw {
        private String userLoginId;
        private String newPassword;
        private String certificatedNumber;
    }

    @NoArgsConstructor
    @Getter
    public static class ChangeMemberPw {
        private String currentPassword;
        private String newPassword;
        private boolean willChangeNextTime;

        public ChangeMemberPw(String currentPassword, String newPassword) {
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
            this.willChangeNextTime = false;
        }
    }
}

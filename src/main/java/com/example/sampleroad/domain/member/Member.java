package com.example.sampleroad.domain.member;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.dto.request.MemberRequestDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MEMBER")
public class Member extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "NICKNAME")
    private String nickname;

    @Column(name = "MEMBER_NO")
    private String memberNo;

    @Column(name = "PUSH_NOTIFICATION_AGREED")
    private Boolean pushNotificationAgreed;

    @Column(name = "JOIN_TERMS_AGREEMENTS")
    private String joinTermsAgreements;

    @Column(name = "MOBILE_NO")
    private String mobileNo;

    @Column(name = "MEMBER_NAME")
    private String memberName;

    @Column(name = "CI")
    private String ci;

    @Column(name = "BIRTHDAY")
    private String birthday;

    @Column(name = "MEMBER_LOGIN_ID")
    private String memberLoginId;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SEX")
    private String sex;

    @Column(name = "SHOP_BY_ACCESS_TOKEN")
    private String shopByAccessToken;

    @Column(name = "EMAIL")
    private String email;

    private Boolean withdrawal;

    @Column(name = "PROFILE_IMAGEURL")
    private String profileImageURL;

    @Column(name = "WITHDRAWAL_DATE")
    private LocalDateTime withdrawalDate;

    @Column(name = "REGISTER_TYPE")
    @Enumerated(EnumType.STRING)
    private RegisterType registerType;

    @Builder
    public Member(String nickname, String memberNo, boolean pushNotificationAgreed,
                  String joinTermsAgreements, String mobileNo, String memberName,
                  String ci, String birthday, String memberLoginId, String password,
                  String sex, String shopByAccessToken, String email, RegisterType registerType) {
        this.memberNo = memberNo;
        this.pushNotificationAgreed = pushNotificationAgreed;
        this.joinTermsAgreements = joinTermsAgreements;
        this.mobileNo = mobileNo;
        this.memberName = memberName;
        this.ci = ci;
        this.birthday = birthday;
        this.memberLoginId = memberLoginId;
        this.password = password;
        this.nickname = nickname;
        this.sex = sex;
        this.shopByAccessToken = shopByAccessToken;
        this.email = email;
        this.withdrawal = false;
        this.registerType = registerType;
    }

    public void updateMemberInfo(MemberRequestDto.UpdateMember dto) {
        if (dto.getEmail() != null) {
            this.email = dto.getEmail();
        }
        if (dto.getNickname() != null) {
            this.nickname = dto.getNickname();
        }
        if (dto.getMemberName() != null) {
            this.memberName = dto.getMemberName();
        }
        if (dto.getMobileNo() != null) {
            this.mobileNo = dto.getMobileNo();
        }
        if (dto.getBirthday() != null) {
            this.birthday = dto.getBirthday();
        }
        if (dto.getSex() != null) {
            this.sex = dto.getSex();
        }
        if (dto.getPushNotificationAgreed() != null) {
            this.pushNotificationAgreed = dto.getPushNotificationAgreed();
        }
    }

    public void updateMemberShopByAccessToken(String accessToken) {
        this.shopByAccessToken = accessToken;
    }

    public void updateMemberWithdrawal() {
        this.withdrawal = true;
    }

    public void updateMemberPassword(String password) {
        this.password = password;
    }

    public void updateMemberMobileNo() {
        this.mobileNo = "";
    }

    public void updateMemberWithdrawalDate(LocalDateTime withdrawalDate) {
        this.withdrawalDate = withdrawalDate;
    }

    public void updateMemberProfileImg(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
}

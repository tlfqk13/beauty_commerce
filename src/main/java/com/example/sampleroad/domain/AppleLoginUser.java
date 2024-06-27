package com.example.sampleroad.domain;

import com.example.sampleroad.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "APPLE_LOGIN_USER")
public class AppleLoginUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLE_LOGIN_USER_ID")
    private Long id;

    @Column(name = "USER_IDENTIFIER")
    @Lob
    String userIdentifier;

    @Column(name = "PASSWORD")
    private String password;

    @ManyToOne()
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public AppleLoginUser(String userIdentifier, String password, Member member) {
        this.userIdentifier = userIdentifier;
        this.password = password;
        this.member = member;
    }
}

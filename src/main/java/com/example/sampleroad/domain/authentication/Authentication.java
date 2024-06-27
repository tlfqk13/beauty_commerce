package com.example.sampleroad.domain.authentication;

import com.example.sampleroad.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "AUTHENTICATION")
public class Authentication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTHENTICATION_ID")
    private Long id;

    @Column(name = "SEND_COUNT")
    private int sendCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    private Authentication(Member member, int sendCount) {
        this.member = member;
        this.sendCount = sendCount;
    }

    public void updateSendCount(int sendCount) {
        this.sendCount = sendCount;
    }
}

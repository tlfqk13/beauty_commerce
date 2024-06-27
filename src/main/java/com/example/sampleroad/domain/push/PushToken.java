package com.example.sampleroad.domain.push;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PUSH_TOKEN")
public class PushToken extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PUSH_TOKEN_ID")
    private Long id;

    @Column(name = "TOKEN")
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public PushToken(Member member,String token){
        this.member = member;
        this.token = token;
    }

    public void updatePushToken(String pushToken) {
        this.token = pushToken;
    }
}

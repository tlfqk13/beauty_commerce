package com.example.sampleroad.domain;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
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
@Table(name = "REFRESH_TOKEN")
public class RefreshToken extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REFRESH_TOKEN_ID")
    private Long id;

    @Column(name = "REFRESH_TOKEN", nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Builder
    public RefreshToken(String token, Member member){
        this.token = token;
        this.member = member;
    }

    public boolean isValid(String token){
        if(!this.token.equals(token)){
            throw new ErrorCustomException(ErrorCode.NO_USER_ERROR);
        }else {
            return true;
        }
    }

    public RefreshToken updateRefreshToken(String token){
        this.token = token;
        return this;
    }
}

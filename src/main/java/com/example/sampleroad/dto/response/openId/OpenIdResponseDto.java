package com.example.sampleroad.dto.response.openId;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OpenIdResponseDto {
    private String loginUrl;

    public OpenIdResponseDto(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    @NoArgsConstructor
    @Getter
    // 주문 상세 조회하기
    public static class OpenIdAccessTokenDto {
        private String accessToken;
        private Long expireIn;
        private OrdinaryMemberResponse ordinaryMemberResponse;

        public OpenIdAccessTokenDto(String accessToken, Long expireIn, OrdinaryMemberResponse ordinaryMemberResponse) {
            this.accessToken = accessToken;
            this.expireIn = expireIn;
            this.ordinaryMemberResponse = ordinaryMemberResponse;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class OrdinaryMemberResponse {
        private String email;
        private String signUpDateTime;

        public OrdinaryMemberResponse(String email, String signUpDateTime) {
            this.email = email;
            this.signUpDateTime = signUpDateTime;
        }
    }
}

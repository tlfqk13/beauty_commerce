package com.example.sampleroad.dto.response.push;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PushResponseQueryDto {

    @NoArgsConstructor
    @Getter
    public static class AgreedMember {
        private Long memberId;
        private String token;
        private String memberName;

        @QueryProjection
        public AgreedMember(Long memberId, String token,String memberName) {
            this.memberId = memberId;
            this.token = token;
            this.memberName = memberName;
        }
    }
}
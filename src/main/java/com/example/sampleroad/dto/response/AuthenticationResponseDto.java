package com.example.sampleroad.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AuthenticationResponseDto {
    private Long id;
    private String memberName;
    private String memberPhone;
    private String userLoginId;
    private int sendCount;

    @QueryProjection
    public AuthenticationResponseDto(Long id, String memberName, String memberPhone, String userLoginId, int sendCount) {
        this.id = id;
        this.memberName = memberName;
        this.memberPhone = memberPhone;
        this.userLoginId = userLoginId;
        this.sendCount = sendCount;
    }
}

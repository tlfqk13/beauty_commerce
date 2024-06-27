package com.example.sampleroad.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CheckMemberTokenResponseDto {
    private boolean isSurvey;

    public CheckMemberTokenResponseDto(boolean isSurvey) {
        this.isSurvey = isSurvey;
    }
}

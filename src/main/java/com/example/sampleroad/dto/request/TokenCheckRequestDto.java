package com.example.sampleroad.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class TokenCheckRequestDto {

    @NoArgsConstructor
    @Getter
    public static class PushToken {
        private String pushToken;
        private String version;
    }
}

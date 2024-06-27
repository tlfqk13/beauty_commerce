package com.example.sampleroad.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ClientIdRequestDto {

    @NoArgsConstructor
    @Getter
    public static class Match{
        private String ci;
    }
}

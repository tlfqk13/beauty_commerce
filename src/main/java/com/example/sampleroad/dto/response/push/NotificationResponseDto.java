package com.example.sampleroad.dto.response.push;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotificationResponseDto {
    private Boolean isFirst;

    public NotificationResponseDto(Boolean isFirst) {
        this.isFirst = isFirst;
    }
}

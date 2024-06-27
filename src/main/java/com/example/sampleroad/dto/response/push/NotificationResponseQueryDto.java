package com.example.sampleroad.dto.response.push;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotificationResponseQueryDto {

    private Long memberId;
    private Boolean adNotificationAgreed;
    private Boolean isFirst;

    @QueryProjection
    public NotificationResponseQueryDto(Long memberId, Boolean adNotificationAgreed, Boolean isFirst) {
        this.memberId = memberId;
        this.adNotificationAgreed = adNotificationAgreed;
        this.isFirst = isFirst;
    }
}

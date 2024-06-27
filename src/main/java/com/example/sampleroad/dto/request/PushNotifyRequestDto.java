package com.example.sampleroad.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PushNotifyRequestDto {
    private Boolean infoSmsEmailNotificationAgreed;
    private Boolean infoAdPushNotificationAgreed;
}

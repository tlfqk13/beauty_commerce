package com.example.sampleroad.dto.response.push;

import com.example.sampleroad.domain.push.PushDataType;
import com.example.sampleroad.domain.push.PushType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PushSendResponseQueryDto {

    private Long pushHistoryId;
    private Long pushId;
    private boolean isRead;
    private PushType pushType;
    private PushDataType pushDataType;
    private Long memberId;

    @QueryProjection
    public PushSendResponseQueryDto(Long pushHistoryId, Long pushId, boolean isRead, PushType pushType,
                                    PushDataType pushDataType, Long memberId) {
        this.pushHistoryId = pushHistoryId;
        this.pushId = pushId;
        this.isRead = isRead;
        this.pushType = pushType;
        this.pushDataType = pushDataType;
        this.memberId = memberId;
    }
}

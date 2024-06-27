package com.example.sampleroad.repository.push;

import com.example.sampleroad.dto.response.push.PushSendResponseQueryDto;

import java.util.List;

public interface PushSendHistoryRepositoryCustom {
    List<PushSendResponseQueryDto> findByMemberId(Long memberId);
}

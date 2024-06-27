package com.example.sampleroad.repository.push;

import com.example.sampleroad.dto.response.push.PushResponseQueryDto;

import java.util.List;
import java.util.Set;

public interface PushTokenRepositoryCustom {
    Set<PushResponseQueryDto.AgreedMember> findByMemberIds(List<Long> memberIds);
    List<PushResponseQueryDto.AgreedMember> findByMemberIdsList(List<Long> memberIds);
    boolean existsByMemberId(Long memberId);
}

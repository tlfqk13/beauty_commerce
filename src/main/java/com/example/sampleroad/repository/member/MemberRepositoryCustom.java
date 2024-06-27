package com.example.sampleroad.repository.member;

import com.example.sampleroad.dto.response.member.MemberQueryDto;

public interface MemberRepositoryCustom {
    MemberQueryDto.MemberInfo findMemberInfo(Long id);
    boolean existsByMemberNo(String memberNo);


}

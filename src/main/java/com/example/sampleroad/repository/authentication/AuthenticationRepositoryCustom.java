package com.example.sampleroad.repository.authentication;

import com.example.sampleroad.dto.response.AuthenticationResponseDto;

public interface AuthenticationRepositoryCustom {
    AuthenticationResponseDto findByMobileNoAndMebmerName(String notiAccount, String memberName);
    boolean existsByMobileNoAndMemberName(String notiAccount, String memberName);
}

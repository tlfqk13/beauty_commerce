package com.example.sampleroad.repository.token;

public interface RefreshTokenRepositoryCustom {
    boolean existsByMemberId(Long memberId);

}

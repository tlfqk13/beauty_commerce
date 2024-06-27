package com.example.sampleroad.repository.token;

import com.example.sampleroad.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long>,RefreshTokenRepositoryCustom {

    Optional<RefreshToken> findByMemberId(Long memberId);
    Optional<RefreshToken> findByToken(String token);
}

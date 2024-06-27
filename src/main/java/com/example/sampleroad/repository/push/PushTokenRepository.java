package com.example.sampleroad.repository.push;

import com.example.sampleroad.domain.push.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<PushToken,Long>, PushTokenRepositoryCustom {
    Optional<PushToken> findFirstByMemberId(Long memberId);
    Optional<PushToken> findByToken(String pushToken);
}

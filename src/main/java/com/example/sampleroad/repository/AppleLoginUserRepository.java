package com.example.sampleroad.repository;

import com.example.sampleroad.domain.AppleLoginUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppleLoginUserRepository extends JpaRepository<AppleLoginUser,Long> {
    Optional<AppleLoginUser> findByUserIdentifier(String userIdentifier);
    Optional<AppleLoginUser> findByMemberId(Long memberId);
}

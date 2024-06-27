package com.example.sampleroad.repository;

import com.example.sampleroad.domain.member.MemberBank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberBankRepository extends JpaRepository<MemberBank,Long> {

    Optional<MemberBank> findByMemberId(Long id);
}

package com.example.sampleroad.repository.experience;

import com.example.sampleroad.domain.experience.ExperienceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExperienceMemberRepository extends JpaRepository<ExperienceMember, Long>, ExperienceMemberRepositoryCustom {
    List<ExperienceMember> findByMemberId(Long memberId);
    boolean existsByMemberIdAndExperienceId(Long memberId,Long experienceId);
    Optional<ExperienceMember> findByExperienceIdAndMemberId(Long experienceId, Long memberId);
    Optional<ExperienceMember> findByExperienceIdAndMemberIdAndIsWinner(Long experienceId, Long memberId, boolean isWinner);
    boolean existsByMemberIdAndExperienceIdAndIsWinner(Long experienceId, Long memberId, boolean isWinner);
}

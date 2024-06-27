package com.example.sampleroad.repository.experience;

import com.example.sampleroad.dto.response.experience.ExperienceResponseQueryDto;

import java.util.List;

public interface ExperienceMemberRepositoryCustom {
    List<ExperienceResponseQueryDto.ExperienceMemberInfo> findExperienceMemberByMemberId(Long id, List<Long> experienceIds);
    long findByExperienceId(Long experienceId);

    List<ExperienceResponseQueryDto.ExperienceMemberInfo> findByIsWinner(Long experienceId, boolean isWinner);

    List<ExperienceResponseQueryDto.ExperienceMemberInfo> findExperienceMemberByExperienceId(Long experienceId);

}

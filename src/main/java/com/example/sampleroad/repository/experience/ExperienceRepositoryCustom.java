package com.example.sampleroad.repository.experience;

import com.example.sampleroad.domain.experience.ExperienceStatus;
import com.example.sampleroad.dto.response.experience.ExperienceResponseQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExperienceRepositoryCustom {
    Page<ExperienceResponseQueryDto.ExperienceInfo> findExperience(Pageable pageable, Long memberId, List<ExperienceStatus> experienceStatus);
    Page<ExperienceResponseQueryDto.ExperienceInfo> findExperience(Pageable pageable, ExperienceStatus experienceStatus);

}

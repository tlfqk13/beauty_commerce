package com.example.sampleroad.repository.experience;

import com.example.sampleroad.domain.experience.ExperienceDetailImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceDetailImageRepository extends JpaRepository<ExperienceDetailImage, Long> {

    List<ExperienceDetailImage> findExperienceDetailImageByExperienceId(Long experienceId);
}

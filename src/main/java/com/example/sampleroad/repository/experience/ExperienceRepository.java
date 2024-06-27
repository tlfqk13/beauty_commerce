package com.example.sampleroad.repository.experience;

import com.example.sampleroad.domain.experience.Experience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Long>, ExperienceRepositoryCustom {
    Page<Experience> findByIsVisible(boolean isVisible, Pageable pageable);
}

package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.domain.CategoryType;
import com.example.sampleroad.domain.survey.ZeroExperienceSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZeroExperienceSurveyRepository extends JpaRepository<ZeroExperienceSurvey,Long>,ZeroExperienceSurveyRepositoryCustom {
    Optional<ZeroExperienceSurvey> findByCategoryType(CategoryType categoryType);
}

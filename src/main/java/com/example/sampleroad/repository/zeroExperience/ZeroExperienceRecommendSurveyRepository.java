package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.domain.survey.ZeroExperienceRecommendSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZeroExperienceRecommendSurveyRepository extends JpaRepository<ZeroExperienceRecommendSurvey, Long>, ZeroExperienceRecommendSurveyRepositoryCustom {
    Optional<ZeroExperienceRecommendSurvey> findByOrdersItem_IdAndMember_Id(Long ordersItemId,Long memberId);
}

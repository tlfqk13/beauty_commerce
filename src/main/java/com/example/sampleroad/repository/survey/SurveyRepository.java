package com.example.sampleroad.repository.survey;

import com.example.sampleroad.domain.survey.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyRepositoryCustom {

    Optional<Survey> findByMemberId(Long id);

}

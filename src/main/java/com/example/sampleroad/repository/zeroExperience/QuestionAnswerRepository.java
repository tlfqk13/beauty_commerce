package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.domain.survey.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long>, QuestionAnswerRepositoryCustom {

    List<QuestionAnswer> findByIdIn(List<Long> ids);

}

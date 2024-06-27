package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.domain.survey.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question,Long> {
}

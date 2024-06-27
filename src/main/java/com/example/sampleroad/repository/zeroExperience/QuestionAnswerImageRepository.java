package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.domain.survey.QuestionAnswerImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionAnswerImageRepository extends JpaRepository<QuestionAnswerImage,Long> {

    List<QuestionAnswerImage> findByQuestionAnswer_Id(Long questionAnswerId);

}

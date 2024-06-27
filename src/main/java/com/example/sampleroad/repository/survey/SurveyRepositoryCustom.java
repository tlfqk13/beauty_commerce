package com.example.sampleroad.repository.survey;

import com.example.sampleroad.dto.response.survey.SurveyQueryDto;

import java.util.Collection;
import java.util.List;

public interface SurveyRepositoryCustom {
    List<SurveyQueryDto.SurveyWithMember> findSurveyWithMember(List<String> memberNos);
}

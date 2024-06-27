package com.example.sampleroad.service;

import com.example.sampleroad.common.exception.ErrorCode;
import com.example.sampleroad.common.exception.ErrorCustomException;
import com.example.sampleroad.domain.survey.Survey;
import com.example.sampleroad.dto.request.SurveyRequestDto;
import com.example.sampleroad.dto.response.survey.SurveyResponseDto;
import com.example.sampleroad.jwt.UserDetailsImpl;
import com.example.sampleroad.repository.survey.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SurveyService {

    private final SurveyRepository surveyRepository;


    @Transactional
    public void addSurvey(SurveyRequestDto.Create dto, UserDetailsImpl userDetails) {

        Optional<Survey> optSurvey = surveyRepository.findByMemberId(userDetails.getMember().getId());

        if (optSurvey.isPresent()) {
            throw new ErrorCustomException(ErrorCode.ALREADY_SURVEY_ERROR);
        }

        String skinType = String.join(",", dto.getSkinType());
        String skinTrouble = String.join(",", dto.getSkinTrouble());
        String preference = String.join(",", dto.getPreference());

        Survey survey = dto.toEntity(userDetails.getMember(), skinType, skinTrouble, preference);
        surveyRepository.save(survey);

    }

    public SurveyResponseDto.AllSurveyByShopbyMemberId getSurvey(UserDetailsImpl userDetails) {

        Survey survey = surveyRepository.findByMemberId(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_SURVEY_FOUND));

        String[] skinTroubleList = survey.getSkinTrouble().split(",");
        return new SurveyResponseDto.AllSurveyByShopbyMemberId(survey, skinTroubleList);
    }

    @Transactional
    public void modifySurvey(SurveyRequestDto.Create dto, UserDetailsImpl userDetails) {

        Survey survey = surveyRepository.findByMemberId(userDetails.getMember().getId())
                .orElseThrow(() -> new ErrorCustomException(ErrorCode.NO_SURVEY_FOUND));

        SurveyResponseDto surveyResponseDto = new SurveyResponseDto(dto.getSkinTrouble(), dto.getSkinType(), dto.getPreference());
        survey.updateMemberInfo(surveyResponseDto);

        surveyRepository.save(survey);

    }
}

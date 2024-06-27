package com.example.sampleroad.dto.response.survey;

import com.example.sampleroad.common.utils.StringConvert;
import com.example.sampleroad.domain.survey.Survey;
import com.example.sampleroad.dto.response.member.MemberQueryDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SurveyResponseDto {
    private String[] skinTrouble;
    private String[] skinType;
    private String[] preference;

    public SurveyResponseDto(String[] skinTrouble, String[] skinType, String[] preference) {
        this.skinTrouble = skinTrouble;
        this.skinType = skinType;
        this.preference = preference;
    }

    public SurveyResponseDto(MemberQueryDto.MemberInfo memberInfo) {
        this.skinTrouble = StringConvert.StringToStringArray(memberInfo.getSkinTrouble());
        this.skinType = StringConvert.StringToStringArray(memberInfo.getSkinType());
        this.preference = StringConvert.StringToStringArray(memberInfo.getPreference());
    }

    @NoArgsConstructor
    @Getter
    public static class AllSurveyByShopbyMemberId{
        private String[] skinTrouble;
        private String skinType;
        private String preference;
        private String shopByMemberId;
        public AllSurveyByShopbyMemberId(Survey survey, String[] skinTrouble){
            this.skinTrouble = skinTrouble;
            this.skinType = survey.getSkinType();
            this.preference = survey.getPreference();
            this.shopByMemberId = survey.getMember().getMemberNo();
        }
    }
}

package com.example.sampleroad.dto.request;

import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.domain.survey.Survey;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SurveyRequestDto {

    @NoArgsConstructor
    @Getter
    public static class Create{
        private String[]skinType;

        private String[] skinTrouble;

        private String[] preference;

        private String memberNo;

        public Survey toEntity(Member member,String skinType, String skinTrouble, String preference){
            return Survey.builder()
                    .skinType(skinType)
                    .skinTrouble(skinTrouble)
                    .preference(preference)
                    .member(member)
                    .build();
        }
    }
}

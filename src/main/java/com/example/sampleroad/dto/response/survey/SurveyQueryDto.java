package com.example.sampleroad.dto.response.survey;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SurveyQueryDto {

    @NoArgsConstructor
    @Getter
    public static class SurveyWithMember {
        private Long memberId;
        private String profileImageUrl;
        private String nickName;
        private String skinType;
        private String registerNo;
        private String skinTrouble;

        @QueryProjection
        public SurveyWithMember(Long memberId, String profileImageUrl, String nickName, String skinType,
                                String registerNo, String skinTrouble) {
            this.memberId = memberId;
            this.profileImageUrl = profileImageUrl;
            this.nickName = nickName;
            this.skinType = skinType;
            this.registerNo = registerNo;
            this.skinTrouble = skinTrouble;
        }
    }
}

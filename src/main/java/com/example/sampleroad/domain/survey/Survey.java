package com.example.sampleroad.domain.survey;

import com.example.sampleroad.common.utils.StringConvert;
import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import com.example.sampleroad.dto.response.survey.SurveyResponseDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SURVEY")
@Builder
@AllArgsConstructor
public class Survey extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SURVEY_ID")
    private Long id;

    @Column(name = "SKIN_TYPE")
    private String skinType;

    @Column(name = "SKIN_TROUBLE")
    private String skinTrouble;

    @Column(name = "PREFERENCE")
    private String preference;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Survey(String skinType, String skinTrouble, String preference, Member member) {
        this.skinType = skinType;
        this.skinTrouble = skinTrouble;
        this.preference = preference;
        this.member = member;
    }

    public void updateMemberInfo(SurveyResponseDto survey) {
        if (survey.getSkinType() != null) {
            this.skinType = StringConvert.StringArrayToString(survey.getSkinType());
        }
        if (survey.getSkinTrouble() != null) {
            this.skinTrouble = StringConvert.StringArrayToString(survey.getSkinTrouble());
        }
        if (survey.getPreference() != null) {
            this.preference = StringConvert.StringArrayToString(survey.getPreference());
        }
    }
}

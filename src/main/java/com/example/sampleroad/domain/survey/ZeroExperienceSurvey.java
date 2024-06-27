package com.example.sampleroad.domain.survey;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.CategoryType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ZERO_EXPERIENCE_SURVEY")
public class ZeroExperienceSurvey extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ZERO_EXPERIENCE_SURVEY_ID")
    private Long id;

    @Column(name = "CATEGORY_TYPE")
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;
}



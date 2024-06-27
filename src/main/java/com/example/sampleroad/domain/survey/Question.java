package com.example.sampleroad.domain.survey;

import com.example.sampleroad.common.utils.TimeStamped;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "QUESTION")
public class Question extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_ID")
    private Long id;

    @Column(name = "QUESTION_CONTENT")
    private String questionContent;

    @Column(name = "SELECT_MAX_COUNT")
    private Integer selectMaxCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "QUESTION_TYPE")
    private QuestionType questionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ZERO_EXPERIENCE_SURVEY_ID")
    private ZeroExperienceSurvey zeroExperienceSurvey;
}

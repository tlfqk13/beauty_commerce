package com.example.sampleroad.domain.survey;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "QUESTION_ANSWER_IMAGE")
public class QuestionAnswerImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ANSWER_IMG_URL")
    private String answerImaUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ANSWER_ID")
    private QuestionAnswer questionAnswer;

}


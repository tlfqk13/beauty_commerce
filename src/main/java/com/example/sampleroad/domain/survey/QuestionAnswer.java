package com.example.sampleroad.domain.survey;

import com.example.sampleroad.common.utils.TimeStamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "QUESTION_ANSWER")
public class QuestionAnswer extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_text")
    private String optionText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private Question question;
}

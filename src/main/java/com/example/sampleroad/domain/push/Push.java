package com.example.sampleroad.domain.push;

import com.example.sampleroad.common.utils.TimeStamped;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PUSH")
public class Push extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PUSH_ID")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "productNo")
    private int productNo;

    @Column(name = "author")
    private String author;

    @Column(name = "push_type")
    @Enumerated(EnumType.STRING)
    private PushType pushType;

    @Column(name = "push_data_type")
    @Enumerated(EnumType.STRING)
    private PushDataType pushDataType;

    @Builder
    public Push(String title, String content, PushType pushType, PushDataType pushDataType, int productNo) {
        this.title = title;
        this.content = content;
        this.pushType = pushType;
        this.pushDataType = pushDataType;
        this.productNo = productNo;
    }
}

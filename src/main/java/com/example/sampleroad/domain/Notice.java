package com.example.sampleroad.domain;

import com.example.sampleroad.common.utils.TimeStamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Notice")
public class Notice extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NOTICE_ID")
    private Long id;

    @Column(name = "NOTICE_NO")
    private int noticeNo;

    @Column(name = "NOTICE_TYPE")
    @Enumerated(EnumType.STRING)
    private NoticeType noticeType;

    @Column(name = "CONTENT")
    private String content;


}

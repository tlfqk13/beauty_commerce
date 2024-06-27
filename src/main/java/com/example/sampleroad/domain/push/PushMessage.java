package com.example.sampleroad.domain.push;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PUSH_MESSAGE")
public class PushMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PUSH_MESSAGE_ID")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "PUSH_MESSAGE_TYPE")
    @Enumerated(EnumType.STRING)
    private PushMessageType pushMessageType;

}

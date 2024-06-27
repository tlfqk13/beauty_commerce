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
@Table(name = "PUSH_READ")
public class PushRead extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PUSH_READ_ID")
    private Long id;

    @Column(name = "PUSH_ID")
    private Long pushId;

    @Column(name = "IS_READ")
    private Boolean isRead;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Builder
    public PushRead(Long pushId, Boolean isRead,Long memberId) {
        this.pushId = pushId;
        this.isRead = isRead;
        this.memberId = memberId;
    }
}

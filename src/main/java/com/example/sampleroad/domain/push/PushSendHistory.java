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
@Table(name = "PUSH_SEND_HISTORY")
public class PushSendHistory extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PUSH_SEND_HISTORY_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUSH_ID")
    private Push push;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Builder
    public PushSendHistory(Push push, boolean isRead, Long memberId) {
        this.push = push;
        this.isRead = isRead;
        this.memberId = memberId;
    }

    public void updateIsRead() {
        this.isRead = true;
    }
}

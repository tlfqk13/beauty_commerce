package com.example.sampleroad.domain.popup;

import com.example.sampleroad.common.utils.TimeStamped;
import com.example.sampleroad.domain.member.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "POPUP_MEMBER_VISIBLE")
public class PopUpMemberVisible extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POPUP_MEMBER_VISIBLE_ID")
    private Long id;

    @Column(name = "IS_VISIBLE_TODAY")
    private boolean isVisibleToday;

    @OneToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public boolean isVisibleToday() {
        return isVisibleToday;
    }

    public void updateIsVisibleToday() {
        this.isVisibleToday = false;
    }

    @Builder
    public PopUpMemberVisible(Member member) {
        this.isVisibleToday = false;
        this.member = member;
    }
}

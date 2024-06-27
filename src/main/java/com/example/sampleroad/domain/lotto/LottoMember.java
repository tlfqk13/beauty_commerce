package com.example.sampleroad.domain.lotto;

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
@Table(name = "LOTTO_MEMBER")
public class LottoMember extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOTTO_MEMBER_ID")
    private Long id;

    @Column(name = "LOTTO_APPLY_COUNT")
    private int applyCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Lotto_id")
    private Lotto lotto;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void updateApplyCount() {
        this.applyCount += 1;
    }

    @Builder
    public LottoMember(Member member, Lotto lotto) {
        this.member = member;
        this.lotto = lotto;
        this.applyCount = 1;
    }
}

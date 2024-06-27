package com.example.sampleroad.domain.experience;

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
@Table(name = "EXPERIENCE_MEMBER")
public class ExperienceMember extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXPERIENCE_MEMBER_ID")
    private Long id;

    @Column(name = "EXPERIENCE_STATUS")
    @Enumerated(EnumType.STRING)
    private ExperienceStatus memberExperienceStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXPERIENCE_ID")
    private Experience experience;

    @Column(name = "SNS_ACCOUNT_INFO")
    private String snsAccountInfo;

    @Column(name = "RECEIVER_NAME")
    private String receiverName;

    @Column(name = "RECEIVER_CONTACT")
    private String receiverContact;

    @Column(name = "RECEIVER_ZIPCODE")
    private String receiverZipCode;

    @Column(name = "RECEIVER_ADDRESS")
    private String receiverAddress;

    @Column(name = "RECEIVER_DETAIL_ADDRESS")
    private String receiverDetailAddress;

    @Column(name = "IS_WINNER")
    private boolean isWinner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public ExperienceMember(Experience experience, Member member, String snsAccountInfo,
                            String receiverName, String receiverContact, String receiverZipCode,
                            String receiverAddress, String receiverDetailAddress) {
        this.experience = experience;
        this.member = member;
        this.memberExperienceStatus = ExperienceStatus.FINISH_SUBMIT;
        this.snsAccountInfo = snsAccountInfo;
        this.receiverName = receiverName;
        this.receiverContact = receiverContact;
        this.receiverZipCode = receiverZipCode;
        this.receiverAddress = receiverAddress;
        this.receiverDetailAddress = receiverDetailAddress;
    }

    public boolean getIsWinner() {
        return isWinner;
    }
}

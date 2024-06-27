package com.example.sampleroad.domain.experience;

import com.example.sampleroad.common.utils.TimeStamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "EXPERIENCE")
public class Experience extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXPERIENCE_ID")
    private Long id;

    @Column(name = "IMAGE_URL")
    @Lob
    private String imageUrl;

    @Column(name = "IS_VISIBLE")
    private boolean isVisible;

    @Column(name = "EXPERIENCE_STATUS")
    @Enumerated(EnumType.STRING)
    private ExperienceStatus experienceStatus;

    @Column(name = "EXPERIENCE_VIEW_COUNT")
    private int experienceViewCount;

    @Column(name = "BRAND_NAME")
    private String brandName;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "OFFER_TARGET")
    private String offerTarget;

    @Column(name = "SNSINFO_PLACEHOLDER")
    private String snsInfoPlaceHolder;

    @Column(name = "PRODUCT_NO")
    private Integer productNo;

    @Column(name = "EXPERIENCE_START_TIME")
    private LocalDateTime experienceStartTime;

    @Column(name = "EXPERIENCE_FINISH_TIME")
    private LocalDateTime experienceFinishTime;

    @Column(name = "WINNER_NOTICE_TIME")
    private LocalDateTime winnerNoticeTime;

    @Column(name = "LIMIT_REGISTER_MEMBER")
    private long limitRegisterMember;

    public boolean getIsVisible() {
        return isVisible;
    }

    public void updateExperienceViewCount() {
        this.experienceViewCount += 1;
    }
}

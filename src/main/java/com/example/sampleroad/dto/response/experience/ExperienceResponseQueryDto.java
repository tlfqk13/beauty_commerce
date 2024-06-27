package com.example.sampleroad.dto.response.experience;

import com.example.sampleroad.domain.experience.ExperienceStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ExperienceResponseQueryDto {

    @NoArgsConstructor
    @Getter
    public static class ExperienceInfo {
        private Long experienceId;
        private String imageUrl;
        private LocalDateTime experienceStartTime;
        private LocalDateTime experienceFinishTime;
        private LocalDateTime winnerNoticeTime;
        private ExperienceStatus experienceStatus;

        @QueryProjection
        public ExperienceInfo(Long experienceId, String imageUrl,
                              LocalDateTime experienceStartTime, LocalDateTime experienceFinishTime, LocalDateTime winnerNoticeTime,
                              ExperienceStatus experienceStatus) {
            this.experienceId = experienceId;
            this.imageUrl = imageUrl;
            this.experienceStartTime = experienceStartTime;
            this.experienceFinishTime = experienceFinishTime;
            this.winnerNoticeTime = winnerNoticeTime;
            this.experienceStatus = experienceStatus;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ExperienceMemberInfo {
        private Long experienceMemberId;
        private ExperienceStatus memberExperienceStatus;
        private Long memberId;
        private Long experienceId;

        @QueryProjection
        public ExperienceMemberInfo(Long experienceMemberId, ExperienceStatus memberExperienceStatus, Long memberId, Long experienceId) {
            this.experienceMemberId = experienceMemberId;
            this.memberExperienceStatus = memberExperienceStatus;
            this.memberId = memberId;
            this.experienceId = experienceId;
        }
    }
}

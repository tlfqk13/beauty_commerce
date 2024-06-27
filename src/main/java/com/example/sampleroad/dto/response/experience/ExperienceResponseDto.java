package com.example.sampleroad.dto.response.experience;

import com.example.sampleroad.domain.experience.ExperienceMember;
import com.example.sampleroad.domain.experience.ExperienceStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ExperienceResponseDto {

    Long totalCount;
    List<ExperienceInfo> experienceList;

    public ExperienceResponseDto(List<ExperienceInfo> experienceList, Long totalCount) {
        this.totalCount = totalCount;
        this.experienceList = experienceList;
    }

    @NoArgsConstructor
    @Getter
    public static class ExperienceInfo {
        private Long experienceId;
        private String imageUrl;
        private String experienceStartTime;
        private String experienceFinishTime;
        private ExperienceStatus experienceStatus;
        private boolean isMyExperience;

        public boolean getIsMyExperience() {
            return isMyExperience;
        }

        public ExperienceInfo(Long experienceId, String imageUrl, String experienceStartTime, String experienceFinishTime,
                              ExperienceStatus experienceStatus, boolean isMyExperience) {
            this.experienceId = experienceId;
            this.imageUrl = imageUrl;
            this.experienceStartTime = experienceStartTime;
            this.experienceFinishTime = experienceFinishTime;
            this.experienceStatus = experienceStatus;
            this.isMyExperience = isMyExperience;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ExperienceDetail {
        private Long experienceId;
        // TODO: 2023/12/07 내가 참여했는지 안했는지
        private boolean isMyExperience;
        private String mainImaUrl;
        private List<String> detailImageUrls;
        private String title;
        private String brandName;
        private String productName;
        private Integer productNo;
        private String appProductImageUrl;
        private String experienceStartTime;
        private String experienceFinishTime;
        private String winnersNoticeTime;
        private long limitRegisterMember;
        private ExperienceStatus memberExperienceStatus;
        private String offerTarget;
        private String snsInfoPlaceHolder;

        public ExperienceDetail(Long experienceId, boolean isMyExperience,
                                String mainImaUrl, List<String> detailImageUrls,
                                String title, String brandName, String productName,
                                Integer productNo, String appProductImageUrl,
                                String experienceStartTime, String experienceFinishTime, String winnersNoticeTime,
                                long limitRegisterMember, ExperienceStatus memberExperienceStatus,
                                String offerTarget, String snsInfoPlaceHolder) {
            this.experienceId = experienceId;
            this.isMyExperience = isMyExperience;
            this.mainImaUrl = mainImaUrl;
            this.detailImageUrls = detailImageUrls;
            this.title = title;
            this.brandName = brandName;
            this.productName = productName;
            this.productNo = productNo;
            this.appProductImageUrl = appProductImageUrl;
            this.experienceStartTime = experienceStartTime;
            this.experienceFinishTime = experienceFinishTime;
            this.winnersNoticeTime = winnersNoticeTime;
            this.limitRegisterMember = limitRegisterMember;
            this.memberExperienceStatus = memberExperienceStatus;
            this.offerTarget = offerTarget;
            this.snsInfoPlaceHolder = snsInfoPlaceHolder;
        }

        public boolean getIsMyExperience() {
            return isMyExperience;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class ExperienceRegisterCheck {
        private String receiverName;
        private String receiverContact;
        private String receiverZipCode;
        private String receiverAddress;
        private String receiverDetailAddress;
        private String snsAccountInfo;
        private String winnersNoticeTime;

        public ExperienceRegisterCheck(ExperienceMember experienceMember, String winnersNoticeTime) {
            this.receiverName = experienceMember.getReceiverName();
            this.receiverContact = experienceMember.getReceiverContact();
            this.receiverZipCode = experienceMember.getReceiverZipCode();
            this.receiverAddress = experienceMember.getReceiverAddress();
            this.receiverDetailAddress = experienceMember.getReceiverDetailAddress();
            this.snsAccountInfo = experienceMember.getSnsAccountInfo();
            this.winnersNoticeTime = winnersNoticeTime;
        }
    }
}

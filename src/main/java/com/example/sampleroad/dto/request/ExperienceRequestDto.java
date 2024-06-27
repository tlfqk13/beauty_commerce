package com.example.sampleroad.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ExperienceRequestDto {
    private Long experienceId;
    private String receiverName;
    private String receiverContact;
    private String receiverZipCode;
    private String receiverAddress;
    private String receiverDetailAddress;
    private String snsAccountInfo;
}

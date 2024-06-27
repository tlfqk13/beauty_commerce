package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceQuestionQueryDto;

import java.util.List;

public interface ZeroExperienceQuestionItemRepositoryCustom {
    List<ZeroExperienceQuestionQueryDto.NecessaryOrdersItem> findOrderItem(List<Long> ordersItemIds, Long memberId);

    boolean existsByMemberId(Long memberId);
}

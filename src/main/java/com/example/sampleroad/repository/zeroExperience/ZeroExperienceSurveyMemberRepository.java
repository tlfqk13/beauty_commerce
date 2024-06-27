package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.domain.survey.ZeroExperienceSurveyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZeroExperienceSurveyMemberRepository extends JpaRepository<ZeroExperienceSurveyMember,Long> {

    List<ZeroExperienceSurveyMember> findByMemberIdAndOrdersItem_IdIn(Long memberId, List<Long> ordersItemId);

}

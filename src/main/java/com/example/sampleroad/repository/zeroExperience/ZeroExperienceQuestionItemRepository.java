package com.example.sampleroad.repository.zeroExperience;

import com.example.sampleroad.domain.order.OrdersItem;
import com.example.sampleroad.domain.survey.ZeroExperienceQuestionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ZeroExperienceQuestionItemRepository extends JpaRepository<ZeroExperienceQuestionItem, Long>, ZeroExperienceQuestionItemRepositoryCustom {

    Optional<ZeroExperienceQuestionItem> findByOrdersItem_Id(Long ordersItemId);
}

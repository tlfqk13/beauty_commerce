package com.example.sampleroad.repository.orders;

import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.dto.response.order.OrdersItemQueryDto;
import com.example.sampleroad.dto.response.zeroExperienceReview.ZeroExperienceRecommendSurveyQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrdersItemRepositoryCustom {
    List<OrdersItemQueryDto> findByOrderNoAndMemberIdAndOrderStatus(String orderNo, Long memberId, OrderStatus orderStatus);

    Boolean existsByOrderNo(String orderNo, Long memberId);

    List<OrdersItemQueryDto> findAllOrdersItem(Long id, Long id1);
    Page<ZeroExperienceRecommendSurveyQueryDto.OrdersItemInfo> findOrdersItemByMemberIdAndKitCategory(Pageable pageable, Long memberId);
    OrdersItemQueryDto.OrderIdByOrdersItem findOrderIdByOrdersItemId(List<Integer> orderOptionNos, Long id);
}

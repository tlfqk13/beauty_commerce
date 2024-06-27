package com.example.sampleroad.repository.orders;

import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.dto.response.order.OrdersQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepositoryCustom {
    List<OrdersQueryDto> findByMemberIds(List<Long> memberIds);
    boolean existsByMemberIdAndOrderStatus(Long memberId, List<OrderStatus> orderStatus, LocalDateTime eventStartDate, LocalDateTime eventEndDate);
    List<OrdersQueryDto.OrderCntQueryDto> findOrderCntByOrderNos(List<String> orderNos, Long memberId);
    Page<OrdersQueryDto> findOrderIn7days(Pageable pageable);
}

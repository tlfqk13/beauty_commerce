package com.example.sampleroad.repository.orders;

import com.example.sampleroad.domain.order.OrderStatus;
import com.example.sampleroad.domain.order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long>, OrdersRepositoryCustom {
    Optional<Orders> findByOrderNoAndMemberIdAndIsMadeOrdersItem(String orderNo, Long memberId, boolean isMadeOrdersItem);
    Optional<Orders> findByOrderNoAndMemberId(String orderNo, Long memberId);
    boolean existsByMemberIdAndOrderStatusIn(Long memberId, List<OrderStatus> orderStatus);
}

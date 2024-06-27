package com.example.sampleroad.repository.orders;

import com.example.sampleroad.domain.order.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrdersItemRepository extends JpaRepository<OrdersItem, Long>, OrdersItemRepositoryCustom {
    @Modifying(clearAutomatically = true)
    @Query("delete from OrdersItem oi where oi.id in :ids")
    void deleteAllByIdInQuery(@Param("ids") List<Long> ordersItemId);
    Optional<OrdersItem> findByProductIdAndOrdersId(Long productId,Long ordersId);
    List<OrdersItem> findByOrders_Id(Long ordersId);
}

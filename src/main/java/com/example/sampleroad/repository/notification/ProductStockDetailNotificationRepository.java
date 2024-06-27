package com.example.sampleroad.repository.notification;

import com.example.sampleroad.domain.notify.ProductStockNotifyDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductStockDetailNotificationRepository extends JpaRepository<ProductStockNotifyDetail,Long> {
    List<ProductStockNotifyDetail> findByProduct_ProductNoIn(List<Integer> productNos);
    Optional<ProductStockNotifyDetail> findByMemberIdAndProduct_ProductNo(Long memberId, int productNo);

    boolean existsByMemberId(Long memberId);

}

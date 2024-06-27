package com.example.sampleroad.repository.notification;

import com.example.sampleroad.domain.notify.ProductStockNotify;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductStockNotificationRepository extends JpaRepository<ProductStockNotify,Long> {
    boolean existsByMemberId(Long memberId);

    Optional<ProductStockNotify> findByMemberId(Long memberId);

}

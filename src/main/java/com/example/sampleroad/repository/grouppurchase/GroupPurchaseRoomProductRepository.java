package com.example.sampleroad.repository.grouppurchase;

import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoomProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupPurchaseRoomProductRepository extends JpaRepository<GroupPurchaseRoomProduct, Long> {
    Optional<GroupPurchaseRoomProduct> findByProduct_ProductNo(int productNo);
}

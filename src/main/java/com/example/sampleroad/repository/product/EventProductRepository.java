package com.example.sampleroad.repository.product;

import com.example.sampleroad.domain.product.EventProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventProductRepository extends JpaRepository<EventProduct, Long>, EventProductRepositoryCustom {
}

package com.example.sampleroad.repository.product;

import com.example.sampleroad.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
    Optional<Product> findByProductNoAndProductInvisible(int productNo, boolean isInvisible);
    List<Product> findByProductNoIn(List<Integer> productNos);
    List<Product> findByProductInvisible(boolean isInvisible);
    List<Product> findByProductNoIn(Set<Integer> productNos);
}

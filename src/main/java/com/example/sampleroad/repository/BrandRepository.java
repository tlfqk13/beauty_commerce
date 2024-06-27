package com.example.sampleroad.repository;

import com.example.sampleroad.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByProduct_ProductNo(int productNo);
}

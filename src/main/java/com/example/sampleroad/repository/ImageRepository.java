package com.example.sampleroad.repository;

import com.example.sampleroad.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}

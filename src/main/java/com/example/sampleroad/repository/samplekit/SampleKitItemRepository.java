package com.example.sampleroad.repository.samplekit;

import com.example.sampleroad.domain.sample.SampleKit;
import com.example.sampleroad.domain.sample.SampleKitItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SampleKitItemRepository extends JpaRepository<SampleKitItem, Long> {

    Optional<SampleKitItem> findBySampleKit(SampleKit sampleKit);

    List<SampleKitItem> findBySampleKitId(Long sampleKitId);
}

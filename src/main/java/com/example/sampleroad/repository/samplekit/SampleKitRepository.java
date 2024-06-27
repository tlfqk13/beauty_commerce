package com.example.sampleroad.repository.samplekit;

import com.example.sampleroad.domain.sample.SampleKit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SampleKitRepository extends JpaRepository<SampleKit, Long>, SampleKitRepositoryCustom {
    List<SampleKit> findByKitProductNoIn(List<Integer> productNos);
}

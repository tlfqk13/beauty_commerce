package com.example.sampleroad.repository;

import com.example.sampleroad.domain.Version;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VersionRepository extends JpaRepository<Version,Long> {
    Optional<Version> findFirstByOsOrderByIdDesc(String os);
    Optional<Version> findByOsAndIsRequiredUpdate(String os,boolean isRequired);
    Optional<Version> findByOsAndIsNotifyUpdate(String os, boolean isNotify);
}

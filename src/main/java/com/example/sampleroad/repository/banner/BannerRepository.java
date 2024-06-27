package com.example.sampleroad.repository.banner;

import com.example.sampleroad.domain.banner.Banner;
import com.example.sampleroad.domain.banner.BannerSectionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long>, BannerRepositoryCustom {
}

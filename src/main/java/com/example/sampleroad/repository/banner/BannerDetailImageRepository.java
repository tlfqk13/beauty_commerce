package com.example.sampleroad.repository.banner;

import com.example.sampleroad.domain.banner.BannerDetailImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerDetailImageRepository extends JpaRepository<BannerDetailImage, Long> {

    List<BannerDetailImage> findByBanner_Id(Long bannerId);
}

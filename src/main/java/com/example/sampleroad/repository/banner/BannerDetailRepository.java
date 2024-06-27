package com.example.sampleroad.repository.banner;

import com.example.sampleroad.domain.banner.BannerDetail;
import com.example.sampleroad.domain.banner.BannerDetailCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BannerDetailRepository extends JpaRepository<BannerDetailCoupon, Long>, BannerDetailRepositoryCustom {
    Optional<BannerDetail> findByBannerKeyNo(int bannerKeyNo);
}

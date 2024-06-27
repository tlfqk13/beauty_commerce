package com.example.sampleroad.repository.delivery;

import com.example.sampleroad.domain.DeliveryLocation;
import com.example.sampleroad.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryLocationRepository extends JpaRepository<DeliveryLocation,Long>, DeliveryLocationRepositoryCustom {

    List<DeliveryLocation> findByMember(Member member);
    Optional<DeliveryLocation> findByMemberAndDefaultAddress(Member member, boolean isDefault);
    Optional<DeliveryLocation> findByMemberAndDefaultAddressIsTrue(Member member);
}

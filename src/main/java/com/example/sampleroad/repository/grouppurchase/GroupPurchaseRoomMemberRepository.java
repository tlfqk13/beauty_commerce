package com.example.sampleroad.repository.grouppurchase;

import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupPurchaseRoomMemberRepository extends JpaRepository<GroupPurchaseRoomMember,Long>, GroupPurchaseRoomMemberRepositoryCustom {
    Optional<GroupPurchaseRoomMember> findByGroupPurchaseRoom_IdAndMember_Id(Long roomId,Long memberId);
    List<GroupPurchaseRoomMember> findAllByGroupPurchaseRoom_IdAndMember_Id(Long roomId, Long memberId);
    Optional<GroupPurchaseRoomMember> findByGroupPurchaseRoom_IdAndMember_IdAndIsPaymentFinish(Long roomId,Long memberId,boolean isPaymentFinish);
}

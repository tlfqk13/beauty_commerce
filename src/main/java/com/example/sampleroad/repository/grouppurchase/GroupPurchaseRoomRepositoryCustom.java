package com.example.sampleroad.repository.grouppurchase;

import com.example.sampleroad.domain.grouppurchase.GroupPurchaseRoom;
import com.example.sampleroad.dto.response.grouppurchase.GroupPurchaseQueryDto;

import java.util.List;

public interface GroupPurchaseRoomRepositoryCustom  {
    List<GroupPurchaseRoom> findEmptyRoomByMemberId(int productNo, Long memberId);
    List<GroupPurchaseRoom> findEmptyRoom(int productNo);
    List<GroupPurchaseRoom> findGroupPurchaseRoom(int productNo);
    List<GroupPurchaseQueryDto> findByRoomId(Long roomId);
}

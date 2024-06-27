package com.example.sampleroad.repository.grouppurchase;

import com.example.sampleroad.dto.response.grouppurchase.GroupPurchaseQueryDto;

import java.util.List;
import java.util.Optional;

public interface GroupPurchaseRoomMemberRepositoryCustom {
    List<GroupPurchaseQueryDto> findLastOneProduct();
    List<GroupPurchaseQueryDto> findIsFullByRoomId(Long roomId);
    List<GroupPurchaseQueryDto.MemberProfileQueryDto> findByProductNos(List<Integer> productNos);
    List<GroupPurchaseQueryDto.MemberProfileQueryDto> findGroupPurchaseRoomMembers(int productNo);
    List<GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto> findGroupPurchaseRoomMembers(List<Long> roomIds);
    Optional<GroupPurchaseQueryDto.MemberProfileQueryDto> findPaymentFinishGroupPurchaseRoomMembersByOrderNo(String orderNo);
    Optional<GroupPurchaseQueryDto.MemberProfileQueryDto> findGroupPurchaseRoomMembersAllByOrderNo(String orderNo);
    Optional<GroupPurchaseQueryDto.MemberProfileQueryDto> findCancelGroupPurchaseRoomMembersByOrderNo(String orderNo);
    List<GroupPurchaseQueryDto.GroupPurchaseOrderQueryDto> findPaymentFinishGroupPurchaseRoomMembersByOrderNos(List<String> orderNos);
    List<GroupPurchaseQueryDto.GroupPurchaseOrderQueryDto> findGroupPurchaseRoomMembersAllByOrderNos(List<String> orderNos);
    List<GroupPurchaseQueryDto.GroupPurchaseOrderInfoQueryDto> findGroupPurchaseRoomMembers(Long memberId);
}

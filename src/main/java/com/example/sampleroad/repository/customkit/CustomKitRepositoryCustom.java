package com.example.sampleroad.repository.customkit;

import com.example.sampleroad.dto.response.customkit.CustomKitItemQueryDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CustomKitRepositoryCustom {

    List<CustomKitItemQueryDto> findCustomKitItem(boolean isOrdered, Long memberId);
    List<CustomKitItemQueryDto> findCustomKitItemByProductNos(boolean isOrdered, Long memberId,Set<Integer> productNos);
    List<CustomKitItemQueryDto> findCustomKitItemByProductNos(Long memberId,Set<Integer> productNos);
    Optional<CustomKitItemQueryDto> findCustomKitItemAndProductNo(boolean isOrdered, Long memberId, int productNo);
    List<CustomKitItemQueryDto> findByOrderNoIn(Set<String> orderNos);
    List<CustomKitItemQueryDto> findByOrderNoIn(String orderNo);
    List<CustomKitItemQueryDto> findByMemberIdAndProductNos(Long memberId, List<Integer> productNos);
}

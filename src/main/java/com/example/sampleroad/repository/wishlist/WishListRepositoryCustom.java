package com.example.sampleroad.repository.wishlist;

import com.example.sampleroad.dto.response.wishList.WishListQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface WishListRepositoryCustom {
    Page<WishListQueryDto> findProductIdsByMemberId(Long id, Pageable pageRequest);
    boolean existsByProductNoAndMemberId(int productNo, Long memberId);
    List<WishListQueryDto> findByProductNosAndMemberId(List<Integer>productNos,Long memberId);
    List<WishListQueryDto> findByProductNosAndMemberId(Set<Integer> productNos, Long memberId);

}

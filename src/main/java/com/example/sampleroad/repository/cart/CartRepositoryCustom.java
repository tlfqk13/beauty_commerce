package com.example.sampleroad.repository.cart;

import com.example.sampleroad.dto.response.cart.CartQueryDto;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CartRepositoryCustom {
    Optional<CartQueryDto> findCartByMemberIdAndProductNoAndOptionNo(Long memberId, int productNo, int optionNo);
    List<CartQueryDto> findCartByProductNo(int productNo);
    List<CartQueryDto> findCartByCartIds(Long memberId, List<Long> cartIds);
    List<CartQueryDto> findCartInfoByMemberId(Long memberId);
    List<CartQueryDto> findCartInfoByMemberIdAndProductNos(Long memberId,List<Integer>productNos);
    List<Tuple> countByMemberId(Long id);
    boolean existsByMemberId(Long memberId);
}

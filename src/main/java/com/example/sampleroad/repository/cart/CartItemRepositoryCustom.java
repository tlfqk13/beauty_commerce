package com.example.sampleroad.repository.cart;

import com.example.sampleroad.dto.response.cart.CartItemQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartItemRepositoryCustom {
    List<CartItemQueryDto> findByMemberAndCartIdsIn(Long memberId, List<Long> cartIds);
    List<CartItemQueryDto> findByMemberId(Long id);
    void deleteByMemberId(Long id);
    List<CartItemQueryDto> findProductByMemberId(Long memberId);
    Page<CartItemQueryDto> findCartProductOver7days(Pageable pageable);
}

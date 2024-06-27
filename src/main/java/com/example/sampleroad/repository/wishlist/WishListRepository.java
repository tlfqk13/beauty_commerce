package com.example.sampleroad.repository.wishlist;

import com.example.sampleroad.domain.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long>, WishListRepositoryCustom {
    Optional<WishList> findByProductIdAndMemberId(Long productId, Long memberId);
}

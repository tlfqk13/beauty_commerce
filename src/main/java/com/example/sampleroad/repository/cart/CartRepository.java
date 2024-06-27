package com.example.sampleroad.repository.cart;

import com.example.sampleroad.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom {
    @Modifying(clearAutomatically = true)
    @Query("delete from Cart c where c.id in :cartIds")
    void deleteAllByCartIdsInQuery(@Param("cartIds") List<Long> cartIds);

    @Modifying(clearAutomatically = true)
    @Query("delete from Cart c where c.id in :cartIds")
    void deleteAllByCartIdsInQuery(@Param("cartIds") Set<Long> cartIds);

    @Modifying(clearAutomatically = true)
    @Query("delete from Cart c where c.id in :cartId")
    void deleteAllByCartIdsInQuery(@Param("cartId") Long cartId);

    void deleteByMemberId(Long memberId);

    Optional<Cart> findByIdAndMemberId(Long cartId, Long memberId);

    List<Cart> findByOrderSheetNo(String orderSheetNo);
}

package com.example.sampleroad.repository.cart;

import com.example.sampleroad.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CartItemRepository extends JpaRepository<CartItem,Long> , CartItemRepositoryCustom{
    Optional<CartItem> findById(Long cartItemId);
    Optional<CartItem> findByCartIdAndProductOptionNumber(Long cartId, int productOptionNo);

    @Modifying(clearAutomatically = true)
    @Query("delete from CartItem ci where ci.id in :ids")
    void deleteAllByIdInQuery(@Param("ids")List<Long> cartItemIds);

    @Modifying(clearAutomatically = true)
    @Query("delete from CartItem ci where ci.id in :id")
    void deleteAllByIdInQuery(@Param("id")Long cartItemId);

    @Modifying(clearAutomatically = true)
    @Query("delete from CartItem ci where ci.id in :ids")
    void deleteAllByIdInQuery(@Param("ids") Set<Long> cartItemIds);
}

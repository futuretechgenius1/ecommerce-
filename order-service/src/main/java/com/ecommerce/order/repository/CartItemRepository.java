package com.ecommerce.order.repository;

import com.ecommerce.order.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByCartId(Long cartId);
    
    Optional<CartItem> findByCartIdAndItemId(Long cartId, Long itemId);
    
    void deleteByCartIdAndItemId(Long cartId, Long itemId);
    
    void deleteByCartId(Long cartId);
    
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.userId = :userId")
    List<CartItem> findByUserId(@Param("userId") java.util.UUID userId);
    
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.itemId = :itemId")
    long countByItemId(@Param("itemId") Long itemId);
}

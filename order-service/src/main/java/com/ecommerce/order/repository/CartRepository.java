package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    Optional<Cart> findByUserId(UUID userId);
    
    boolean existsByUserId(UUID userId);
    
    @Query("SELECT COUNT(c) FROM Cart c WHERE SIZE(c.items) > 0")
    long countActiveCarts();
    
    @Query("SELECT c FROM Cart c JOIN FETCH c.items WHERE c.userId = :userId")
    Optional<Cart> findByUserIdWithItems(UUID userId);
}

package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    Page<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE " +
           "(:userId IS NULL OR o.userId = :userId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:dateFrom IS NULL OR o.createdAt >= :dateFrom) AND " +
           "(:dateTo IS NULL OR o.createdAt <= :dateTo)")
    Page<Order> findOrdersWithFilters(
        @Param("userId") UUID userId,
        @Param("status") Order.OrderStatus status,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo,
        Pageable pageable
    );
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status IN ('PAID', 'SHIPPED', 'DELIVERED')")
    long countSuccessfulOrders();
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :dateFrom AND o.createdAt <= :dateTo AND o.status IN ('PAID', 'SHIPPED', 'DELIVERED')")
    long countSuccessfulOrdersByDateRange(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status IN ('PAID', 'SHIPPED', 'DELIVERED')")
    BigDecimal getTotalRevenue();
    
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt >= :dateFrom AND o.createdAt <= :dateTo AND o.status IN ('PAID', 'SHIPPED', 'DELIVERED')")
    BigDecimal getTotalRevenueByDateRange(
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo
    );
    
    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);
    
    @Query("SELECT o FROM Order o JOIN FETCH o.payment WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithPayment(@Param("orderNumber") String orderNumber);
}

package com.ecommerce.order.repository;

import com.ecommerce.order.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByOrderId(Long orderId);
    
    Optional<Payment> findByTransactionRef(String transactionRef);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.method.code = :methodCode")
    List<Payment> findByPaymentMethodCode(@Param("methodCode") String methodCode);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'SUCCESS'")
    long countSuccessfulPayments();
}

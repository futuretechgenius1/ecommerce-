package com.ecommerce.order.repository;

import com.ecommerce.order.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    
    Optional<PaymentMethod> findByCode(String code);
    
    List<PaymentMethod> findByActiveTrue();
    
    boolean existsByCode(String code);
}

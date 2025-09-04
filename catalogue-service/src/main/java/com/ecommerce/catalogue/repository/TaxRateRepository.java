package com.ecommerce.catalogue.repository;

import com.ecommerce.catalogue.entity.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {
    
    Optional<TaxRate> findByCategoryId(Long categoryId);
    
    boolean existsByCategoryId(Long categoryId);
}

package com.ecommerce.catalogue.repository;

import com.ecommerce.catalogue.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    Optional<Inventory> findByItemId(Long itemId);
    
    @Query("SELECT i FROM Inventory i WHERE i.stockQty > 0")
    List<Inventory> findItemsInStock();
    
    @Query("SELECT i FROM Inventory i WHERE i.stockQty <= 5")
    List<Inventory> findLowStockItems();
    
    @Query("SELECT i FROM Inventory i WHERE i.stockQty = 0")
    List<Inventory> findOutOfStockItems();
}

package com.ecommerce.catalogue.repository;

import com.ecommerce.catalogue.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    List<Item> findByCategoryId(Long categoryId);
    
    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);
    
    @Query("SELECT i FROM Item i WHERE " +
           "(:q IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
           "(:categoryId IS NULL OR i.category.id = :categoryId) AND " +
           "(:minPrice IS NULL OR i.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR i.price <= :maxPrice) AND " +
           "(:color IS NULL OR LOWER(i.color) = LOWER(:color))")
    Page<Item> findItemsWithFilters(
        @Param("q") String searchQuery,
        @Param("categoryId") Long categoryId,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("color") String color,
        Pageable pageable
    );
    
    List<Item> findByColor(String color);
    
    List<Item> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    @Query("SELECT DISTINCT i.color FROM Item i WHERE i.color IS NOT NULL ORDER BY i.color")
    List<String> findDistinctColors();
    
    @Query("SELECT i FROM Item i JOIN i.inventory inv WHERE inv.stockQty > 0")
    List<Item> findItemsInStock();
    
    boolean existsByNameAndCategoryId(String name, Long categoryId);
}

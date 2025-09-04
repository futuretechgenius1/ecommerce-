package com.ecommerce.catalogue.controller;

import com.ecommerce.catalogue.dto.ItemRequest;
import com.ecommerce.catalogue.dto.ItemResponse;
import com.ecommerce.catalogue.entity.Category;
import com.ecommerce.catalogue.service.CatalogueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
public class CatalogueController {

    @Autowired
    private CatalogueService catalogueService;

    // Public endpoints for browsing catalog
    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> searchItems(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String color,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        
        try {
            Page<ItemResponse> items = catalogueService.searchItems(q, categoryId, minPrice, maxPrice, color, page, size, sortBy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("items", items.getContent());
            response.put("currentPage", items.getNumber());
            response.put("totalItems", items.getTotalElements());
            response.put("totalPages", items.getTotalPages());
            response.put("hasNext", items.hasNext());
            response.put("hasPrevious", items.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        try {
            ItemResponse item = catalogueService.getItemById(id);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = catalogueService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{categoryId}/items")
    public ResponseEntity<List<ItemResponse>> getItemsByCategory(@PathVariable Long categoryId) {
        List<ItemResponse> items = catalogueService.getItemsByCategory(categoryId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/filters/colors")
    public ResponseEntity<List<String>> getAvailableColors() {
        List<String> colors = catalogueService.getAvailableColors();
        return ResponseEntity.ok(colors);
    }

    @GetMapping("/categories/{categoryId}/gst-rate")
    public ResponseEntity<Map<String, BigDecimal>> getGstRate(@PathVariable Long categoryId) {
        BigDecimal gstRate = catalogueService.getGstRate(categoryId);
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("gstPercent", gstRate);
        return ResponseEntity.ok(response);
    }

    // Admin endpoints for managing items
    @PostMapping("/admin/items")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createItem(@Valid @RequestBody ItemRequest request) {
        try {
            ItemResponse item = catalogueService.createItem(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/admin/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        try {
            ItemResponse item = catalogueService.updateItem(id, request);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/admin/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteItem(@PathVariable Long id) {
        try {
            catalogueService.deleteItem(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "catalogue-service");
        return ResponseEntity.ok(response);
    }
}

package com.ecommerce.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "catalogue-service")
public interface CatalogueClient {

    @GetMapping("/api/items/{id}")
    ItemDto getItemById(@PathVariable("id") Long id);

    @GetMapping("/api/categories/{categoryId}/gst-rate")
    Map<String, BigDecimal> getGstRate(@PathVariable("categoryId") Long categoryId);

    // DTO for item response
    class ItemDto {
        private Long id;
        private String name;
        private String model;
        private BigDecimal price;
        private String color;
        private Long categoryId;
        private String categoryName;
        private Integer stockQty;
        private boolean inStock;
        private BigDecimal gstPercent;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public Integer getStockQty() {
            return stockQty;
        }

        public void setStockQty(Integer stockQty) {
            this.stockQty = stockQty;
        }

        public boolean isInStock() {
            return inStock;
        }

        public void setInStock(boolean inStock) {
            this.inStock = inStock;
        }

        public BigDecimal getGstPercent() {
            return gstPercent;
        }

        public void setGstPercent(BigDecimal gstPercent) {
            this.gstPercent = gstPercent;
        }
    }
}

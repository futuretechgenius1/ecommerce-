package com.ecommerce.catalogue.entity;

import javax.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    private Long itemId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private Integer stockQty;

    public Inventory() {}

    public Inventory(Item item, Integer stockQty) {
        this.item = item;
        this.itemId = item.getId();
        this.stockQty = stockQty;
    }

    // Getters and Setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        if (item != null) {
            this.itemId = item.getId();
        }
    }

    public Integer getStockQty() {
        return stockQty;
    }

    public void setStockQty(Integer stockQty) {
        this.stockQty = stockQty;
    }

    public boolean isInStock() {
        return stockQty != null && stockQty > 0;
    }

    public boolean hasStock(int quantity) {
        return stockQty != null && stockQty >= quantity;
    }

    public void reduceStock(int quantity) {
        if (hasStock(quantity)) {
            this.stockQty -= quantity;
        } else {
            throw new RuntimeException("Insufficient stock");
        }
    }

    public void addStock(int quantity) {
        if (this.stockQty == null) {
            this.stockQty = quantity;
        } else {
            this.stockQty += quantity;
        }
    }
}

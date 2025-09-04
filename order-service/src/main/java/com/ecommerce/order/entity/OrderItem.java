package com.ecommerce.order.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "price_at_purchase", precision = 10, scale = 2, nullable = false)
    private BigDecimal priceAtPurchase;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // Cached item details at time of purchase
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_color")
    private String itemColor;

    @Column(name = "item_model")
    private String itemModel;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "gst_percent", precision = 5, scale = 2)
    private BigDecimal gstPercent;

    public OrderItem() {}

    public OrderItem(Order order, Long itemId, BigDecimal priceAtPurchase, Integer quantity) {
        this.order = order;
        this.itemId = itemId;
        this.priceAtPurchase = priceAtPurchase;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemColor() {
        return itemColor;
    }

    public void setItemColor(String itemColor) {
        this.itemColor = itemColor;
    }

    public String getItemModel() {
        return itemModel;
    }

    public void setItemModel(String itemModel) {
        this.itemModel = itemModel;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getGstPercent() {
        return gstPercent;
    }

    public void setGstPercent(BigDecimal gstPercent) {
        this.gstPercent = gstPercent;
    }

    // Helper methods
    public BigDecimal getSubtotal() {
        return priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getGstAmount() {
        if (gstPercent == null) {
            return BigDecimal.ZERO;
        }
        return getSubtotal().multiply(gstPercent).divide(BigDecimal.valueOf(100));
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getGstAmount());
    }
}

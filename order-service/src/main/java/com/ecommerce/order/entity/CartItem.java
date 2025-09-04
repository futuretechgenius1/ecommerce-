package com.ecommerce.order.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // Cached item details for performance
    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_price", precision = 10, scale = 2)
    private BigDecimal itemPrice;

    @Column(name = "item_color")
    private String itemColor;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "gst_percent", precision = 5, scale = 2)
    private BigDecimal gstPercent;

    public CartItem() {}

    public CartItem(Cart cart, Long itemId, Integer quantity) {
        this.cart = cart;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
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

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemColor() {
        return itemColor;
    }

    public void setItemColor(String itemColor) {
        this.itemColor = itemColor;
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
        if (itemPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return itemPrice.multiply(BigDecimal.valueOf(quantity));
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

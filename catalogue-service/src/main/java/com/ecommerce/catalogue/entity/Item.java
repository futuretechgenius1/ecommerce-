package com.ecommerce.catalogue.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column
    private String model;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column
    private String dimensions; // LxWxH in mm

    @Column
    private String color;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemAttribute> attributes;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Inventory inventory;

    public Item() {
        this.createdAt = LocalDateTime.now();
    }

    public Item(Category category, String name, String model, BigDecimal price, String dimensions, String color, String description) {
        this();
        this.category = category;
        this.name = name;
        this.model = model;
        this.price = price;
        this.dimensions = dimensions;
        this.color = color;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ItemAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<ItemAttribute> attributes) {
        this.attributes = attributes;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}

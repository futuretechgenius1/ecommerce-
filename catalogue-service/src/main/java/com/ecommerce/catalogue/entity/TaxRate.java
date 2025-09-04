package com.ecommerce.catalogue.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tax_rates")
public class TaxRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal gstPercent;

    public TaxRate() {}

    public TaxRate(Category category, BigDecimal gstPercent) {
        this.category = category;
        this.gstPercent = gstPercent;
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

    public BigDecimal getGstPercent() {
        return gstPercent;
    }

    public void setGstPercent(BigDecimal gstPercent) {
        this.gstPercent = gstPercent;
    }

    public BigDecimal calculateGst(BigDecimal amount) {
        if (amount == null || gstPercent == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(gstPercent).divide(BigDecimal.valueOf(100));
    }
}

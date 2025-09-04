package com.ecommerce.order.entity;

import javax.persistence.*;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "description")
    private String description;

    public PaymentMethod() {}

    public PaymentMethod(String code, String displayName, Boolean active) {
        this.code = code;
        this.displayName = displayName;
        this.active = active;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Payment method codes
    public static final String UPI = "UPI";
    public static final String COD = "COD";
    public static final String CARD = "CARD";
}

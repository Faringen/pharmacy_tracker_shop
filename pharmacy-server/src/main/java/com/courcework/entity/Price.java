package com.courcework.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "price")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;


    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    protected Price() {
    }

    public Price(Medicine medicine, Pharmacy pharmacy, BigDecimal price) {
        this.medicine = medicine;
        this.pharmacy = pharmacy;
        this.price = price;
    }

    public Long getId() {
        return id;
    }


    public Medicine getMedicine() {
        return medicine;
    }

    public void setDrug(Medicine medicine) {
        this.medicine = medicine;
    }

    public Pharmacy getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(Pharmacy pharmacy) {
        this.pharmacy = pharmacy;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

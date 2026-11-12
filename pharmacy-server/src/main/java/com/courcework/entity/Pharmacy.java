package com.courcework.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pharmacy",
    uniqueConstraints = {@UniqueConstraint(columnNames = "address")})
public class Pharmacy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @Column(name = "website_url")
    private String websiteURL;


    protected Pharmacy(){

    }

    public Pharmacy(String name, String address, String websiteURL) {
        this.name = name;
        this.address = address;
        this.websiteURL = websiteURL;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }
}

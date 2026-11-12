package com.courcework.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "medicine", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active_substance", nullable = false)
    private String activeSubstance;

    @Column(name = "dosage_form")
    private String dosageForm;

    @Column(name = "dosage")
    private String dosageCount;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column
    private String description;



    protected Medicine() {}

    public Medicine(String name, String activeSubstance, String dosageForm, String dosageCount) {
        this.name = name;
        this.activeSubstance = activeSubstance;
        this.dosageForm = dosageForm;
        this.dosageCount = dosageCount;
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

    public String getActiveSubstance() {
        return activeSubstance;
    }

    public void setActiveSubstance(String activeSubstance) {
        this.activeSubstance = activeSubstance;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getDosageCount() {
        return dosageCount;
    }

    public void setDosageCount(String dosageCount) {
        this.dosageCount = dosageCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}

package com.courcework.dto;

import com.courcework.entity.Medicine;

public record MedicineDTO(Long id, String name,
                          String activeSubstance,
                          String dosageForm, String dosage,
                          String manufacturer, String description) {

    public static MedicineDTO from(Medicine medicine){
        return new MedicineDTO(
                medicine.getId(),
                medicine.getName(),
                medicine.getActiveSubstance(),
                medicine.getDosageForm(),
                medicine.getDosageCount(),
                medicine.getManufacturer(),
                medicine.getDescription()
        );
    }

}

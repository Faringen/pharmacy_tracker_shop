package com.courcework.pharmacyclient.dto;

public record MedicineDTO(Long id, String name,
                          String activeSubstance,
                          String dosageForm, String dosage,
                          String manufacturer, String description) {



}

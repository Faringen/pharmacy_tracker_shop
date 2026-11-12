package com.courcework.dto;

import com.courcework.entity.Pharmacy;

public record PharmacyDTO(Long id, String name, String address, String websiteURL) {
    public static PharmacyDTO from(Pharmacy pharmacy){
        return new PharmacyDTO(
                pharmacy.getId(),
                pharmacy.getName(),
                pharmacy.getAddress(),
                pharmacy.getWebsiteURL()
        );
    }
}

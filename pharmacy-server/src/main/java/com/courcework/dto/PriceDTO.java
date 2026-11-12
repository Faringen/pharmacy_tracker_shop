package com.courcework.dto;

import com.courcework.entity.Price;

import java.math.BigDecimal;

public record PriceDTO(String medicineName, String pharmacyName, String websiteUrl, BigDecimal price) {

    public static PriceDTO from(Price priceEn){
        return new PriceDTO(priceEn.getMedicine().getName(), priceEn.getPharmacy().getName(), priceEn.getPharmacy().getWebsiteURL(),
                priceEn.getPrice()
        );
    }
}

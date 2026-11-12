package com.courcework.pharmacyclient.dto;

import java.math.BigDecimal;

public record PriceDTO(String medicineName,String pharmacyName,String websiteUrl,BigDecimal price) {
}

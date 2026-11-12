package com.courcework.controller;

import com.courcework.dto.PriceDTO;
import com.courcework.entity.Price;
import com.courcework.service.PriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/price")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping
    public List<PriceDTO> getPrices(@RequestParam(required = false) Long medicineId,
                                    @RequestParam(required = false) Long pharmacyId) {

        if(medicineId != null) return priceService.findByMedId(medicineId);
        else if (pharmacyId != null) return priceService.findByPharmId(pharmacyId);
        else throw new IllegalArgumentException("Enter a pharmacyID or medicineID.");
    }
}

package com.courcework.controller;

import com.courcework.dto.PharmacyDTO;
import com.courcework.entity.Pharmacy;
import com.courcework.service.PharmacyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
public class PharmacyController {
    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @GetMapping
    public List<PharmacyDTO> getAll(){
        return pharmacyService.getAll();
    }

    @GetMapping("/{id}")
    public PharmacyDTO getById(@PathVariable Long id){
        return pharmacyService.getPharmacyById(id);
    }
}

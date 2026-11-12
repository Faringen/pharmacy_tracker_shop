package com.courcework.controller;


import com.courcework.dto.MedicineDTO;
import com.courcework.entity.Medicine;
import com.courcework.service.MedicineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meds")
public class MedicineController {
    private final MedicineService medService;

    public MedicineController(MedicineService medService) {
        this.medService = medService;
    }

    @GetMapping
    public List<MedicineDTO> getAll(){
        return medService.findAll();
    }

    @GetMapping("/{id}")
    public MedicineDTO getById(@PathVariable Long id){
        return medService.findById(id);
    }

    @GetMapping("/search")
    public List<MedicineDTO> search(@RequestParam String name){
        return medService.findByName(name);
    }

    @GetMapping("/{id}/analogs")
    public List<MedicineDTO> getAnalogs(@PathVariable Long id){
        return medService.findAnalogs(id);
    }

}

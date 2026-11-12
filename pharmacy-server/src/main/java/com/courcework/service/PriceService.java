package com.courcework.service;

import com.courcework.dto.PriceDTO;
import com.courcework.entity.Price;
import com.courcework.repository.PriceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceService {

    private final PriceRepository repository;

    public PriceService(PriceRepository repository) {
        this.repository = repository;
    }

    public List<PriceDTO> findByPharmId(Long pharmId) {
        return repository.findByPharmacyId(pharmId).stream().map(PriceDTO::from).toList();
    }

    public List<PriceDTO> findByMedId(Long medId) {
        return repository.findByMedicineId(medId).stream().map(PriceDTO::from).toList();
    }
}

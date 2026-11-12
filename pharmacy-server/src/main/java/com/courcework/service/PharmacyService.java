package com.courcework.service;

import com.courcework.dto.PharmacyDTO;
import com.courcework.entity.Pharmacy;
import com.courcework.repository.PharmacyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PharmacyService {

    private final PharmacyRepository repository;

    public PharmacyService(PharmacyRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public PharmacyDTO getPharmacyById(Long id) {
        return PharmacyDTO.from(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pharmacy not found: "+ id)));
    }

    @Transactional(readOnly = true)
    public List<PharmacyDTO> getAll() {
        return  repository.findAll().stream().map(PharmacyDTO::from).toList();
    }
}

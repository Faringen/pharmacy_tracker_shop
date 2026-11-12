package com.courcework.service;

import com.courcework.dto.MedicineDTO;
import com.courcework.entity.Medicine;
import com.courcework.repository.MedicineRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicineService {

    private final MedicineRepository repository;

    public MedicineService(MedicineRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MedicineDTO> findAll() {
        return repository.findAll().stream().map(MedicineDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public MedicineDTO findById(Long id) {
        return MedicineDTO.from(repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Medicine not found: " + id)));
    }
    @Transactional(readOnly = true)
    public List<MedicineDTO> findByName(String name) {
        return repository.findByNameContaining(name).stream()
                .map(MedicineDTO::from)
                .toList();
    }
    @Transactional(readOnly = true)
    public List<MedicineDTO> findAnalogs(Long id) {
        Medicine medicine = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found: " + id));
        return repository.findByActiveSubstance(medicine.getActiveSubstance(), id).stream()
                .map(MedicineDTO::from).toList();
    }
}

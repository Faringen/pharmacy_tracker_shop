package com.courcework.repository;

import com.courcework.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByNameContaining(String name);

    List<Medicine> findByActiveSubstanceIgnoreCase(String activeSubstance, Long id);
}

package com.courcework.repository;

import com.courcework.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findByMedicineId(Long medicineId);
    List<Price> findByPharmacyId(Long pharmacyId);
}

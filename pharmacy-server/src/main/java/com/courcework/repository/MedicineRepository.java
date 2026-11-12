package com.courcework.repository;

import com.courcework.entity.Medicine;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    
    @Transactional
    @Modifying
    // Запрос для PostgreSQL / H2
    @Query(value = "DELETE FROM medicine " +
            "WHERE id NOT IN (" +
            "    SELECT MIN(id) " +
            "    FROM medicine " +
            "    GROUP BY name" +
            ")", nativeQuery = true)
    void deleteDuplicateNames();

    List<Medicine> findByNameContaining(String name);

    List<Medicine> findByActiveSubstance(String activeSubstance, Long id);
}

package com.courcework.repository;

import com.courcework.entity.Medicine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest(properties = "spring.sql.init.mode=never")
class MedicineRepositoryTest {

    @Autowired
    private MedicineRepository repository;
    private Medicine paracetamol;
    private Medicine ibuprofen;

    @BeforeEach
    void setUp() {
        paracetamol = repository.save(new Medicine("Парацетамол", "Парацетамол", "таблетки", "500 мг"));
        repository.save(new Medicine("Панадол", "Парацетамол", "таблетки", "500 мг"));
        ibuprofen = repository.save(new Medicine("Ибупрофен", "Ибупрофен", "таблетки", "400 мг"));
    }


    @Test
    void findByNameContaining_substring_findsMedicine() {
        List<Medicine> result = repository.findByNameContaining("цетам");

        assertEquals(1, result.size());
        assertEquals("Парацетамол", result.get(0).getName());
    }



    @Test
    void findByNameContaining_unknownName_returnsEmptyList() {
        assertTrue(repository.findByNameContaining("Несуществующий").isEmpty());
    }

    @Test
    void findByActiveSubstance_IgnoreCase_excludesMedicineItself() {
        List<Medicine> analogs = repository.findByActiveSubstanceIgnoreCase("Парацетамол", paracetamol.getId());

        assertEquals(2, analogs.size());
        assertEquals("Панадол", analogs.getLast().getName());
    }

    @Test

    void findByActiveSubstance_IgnoreCase_noAnalogs_returnsEmptyList() {
        List<Medicine> analogs = repository.findByActiveSubstanceIgnoreCase("Ибупрофен", ibuprofen.getId());

        assertEquals(1, analogs.size());
        assertEquals(ibuprofen.getName(), analogs.getFirst().getName());
    }



    @Test
    void save_duplicateName_throwsException() {
        Medicine duplicate = new Medicine("Панадол", "Парацетамол", "сироп", "120 мг");

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(duplicate));
    }
}
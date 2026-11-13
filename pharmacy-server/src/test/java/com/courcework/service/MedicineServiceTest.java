package com.courcework.service;

import com.courcework.dto.MedicineDTO;
import com.courcework.entity.Medicine;
import com.courcework.repository.MedicineRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {
    @Mock
    private MedicineRepository repository;

    @InjectMocks
    private MedicineService service;

    Medicine medicine;
    Medicine medicine2;

    @BeforeEach
    void setUp(){
        medicine = medicine(1L, "Name", "Active substance");
        medicine2 = medicine(2L, "test 2", "Active substance");
    }

    private Medicine medicine(Long id, String name, String activeSubstance){
        Medicine m = new Medicine(name, activeSubstance, "Dosage","500 g");
        ReflectionTestUtils.setField(m, "id", id);
        return m;
    }

    @Nested
    class UsualResult{
        @Test
        void findAll_returnAllsMedicineDTO() {
            when(repository.findAll()).thenReturn(List.of(medicine, medicine2));

            List<MedicineDTO> result = service.findAll();

            assertEquals(2, result.size());
            assertEquals(medicine.getName(), result.get(0).name());
            assertEquals(medicine2.getName(), result.get(1).name());
        }

        @Test
        void findById_returnMedicineDtoById() {
            Long id = 1L;
            String name = medicine.getName();
            when(repository.findById(id)).thenReturn(Optional.of(medicine));

            MedicineDTO result = service.findById(id);

            assertEquals(id, result.id());
            assertEquals(name, result.name());
        }



        @Test
        void findByName_returnAllFound() {
            String searchRequest = "Na";
            when(repository.findByNameContaining(searchRequest)).thenReturn(List.of(medicine));

            List<MedicineDTO> result = service.findByName(searchRequest);

            assertEquals(1, result.size());
            assertEquals(medicine.getName() , result.get(0).name());
        }

        @Test
        void findByName_noFound_returnEmptyList(){
            String searchRequest = "123";
            when(repository.findByNameContaining(searchRequest)).thenReturn(List.of());

            List<MedicineDTO> result = service.findByName(searchRequest);

            assertTrue(result.isEmpty());
        }

        @Test
        void findAnalogs_returnMedicineDtoByActiveSubstance() {
            Long id = 1L;
            String activeSubstance = "Active substance";
            when(repository.findById(1L)).thenReturn(Optional.of(medicine));
            when(repository.findByActiveSubstance(activeSubstance, id)).thenReturn(List.of(medicine));

            List<MedicineDTO> result = service.findAnalogs(id);

            assertEquals(1, result.size());
            assertEquals(medicine.getName(), result.get(0).name());
            verify(repository).findByActiveSubstance(activeSubstance, id);
        }
    }

    @Nested
    class ExceptionResultTest{
        @Test
        void findById_unknownId_throwEntityNotFoundException() {
            Long id = 99L;
            String eMessage = "Medicine not found: " + id;
            when(repository.findById(id)).thenReturn(Optional.empty());

            EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> service.findById(id));
            assertEquals(eMessage, e.getMessage());
        }

        @Test
        void findAnalogs_unknowMedicineID_throwEntityNotFoundException(){
            Long id = 99L;
            String eMessage = "Medicine not found: " + id;
            when(repository.findById(id)).thenReturn(Optional.empty());

            EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> service.findAnalogs(id));
            assertEquals(eMessage, e.getMessage());
        }
    }
}
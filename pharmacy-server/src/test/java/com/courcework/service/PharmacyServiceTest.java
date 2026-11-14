package com.courcework.service;

import com.courcework.dto.PharmacyDTO;
import com.courcework.entity.Pharmacy;
import com.courcework.repository.PharmacyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PharmacyServiceTest {
    @Mock
    private PharmacyRepository repository;

    @InjectMocks
    private PharmacyService service;

    Pharmacy pharmacy;
    Pharmacy pharmacy2;

    @BeforeEach
    void setUp(){
        pharmacy = pharmacy(1L, "Test Name", "Test Address");
        pharmacy2 = pharmacy(2L, "Test Name 2", "Test Address 2");
    }
    private Pharmacy pharmacy(Long id, String name, String address){
        Pharmacy m = new Pharmacy(name, address, "Test Url");
        ReflectionTestUtils.setField(m, "id", id);
        return m;
    }

    @Test
    void getPharmacyById_returnPharmacyById() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(pharmacy));

        PharmacyDTO result = service.getPharmacyById(id);

        assertEquals(pharmacy.getName(), result.name());
        assertEquals(id, result.id());
    }

    @Test
    void getPharmacyById_unknownID_throwEntityNotFoundException() {
        Long id = 99L;
        String eMessage = "Pharmacy not found: " + id;
        when(repository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> service.getPharmacyById(id));
        assertEquals(eMessage, e.getMessage());;
    }

    @Test
    void getAll_returnAllPharmacy() {
        when(repository.findAll()).thenReturn(List.of(pharmacy, pharmacy2));

        List<PharmacyDTO> result = service.getAll();

        assertNotNull(result);
        assertEquals(pharmacy2.getId(), result.get(1).id());
        assertEquals(pharmacy.getName(), result.get(0).name());
    }
}
package com.courcework.controller;

import com.courcework.dto.PharmacyDTO;
import com.courcework.service.PharmacyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PharmacyControllerTest {

    @Mock
    private PharmacyService pharmacyService;

    @InjectMocks
    private PharmacyController controller;

    private final PharmacyDTO rigla = mock(PharmacyDTO.class);
    private final PharmacyDTO april = mock(PharmacyDTO.class);

    @Test
    @DisplayName("getAll возвращает список аптек от сервиса")
    void getAll_returnsServiceResult() {
        when(pharmacyService.getAll()).thenReturn(List.of(rigla, april));

        List<PharmacyDTO> result = controller.getAll();

        assertEquals(List.of(rigla, april), result);
    }

    @Test
    @DisplayName("getAll: аптек нет, пустой список")
    void getAll_noPharmacies_returnsEmptyList() {
        when(pharmacyService.getAll()).thenReturn(List.of());

        assertTrue(controller.getAll().isEmpty());
    }

    @Test
    @DisplayName("getById передаёт id сервису и возвращает его результат")
    void getById_returnsServiceResult() {
        when(pharmacyService.getPharmacyById(1L)).thenReturn(rigla);

        PharmacyDTO result = controller.getById(1L);

        assertEquals(rigla, result);
        verify(pharmacyService).getPharmacyById(1L);
    }

    @Test
    @DisplayName("getById: аптека не найдена, исключение доходит до обработчика ошибок")
    void getById_notFound_propagatesException() {
        when(pharmacyService.getPharmacyById(99L)).thenThrow(new EntityNotFoundException("Pharmacy not found: 99"));

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> controller.getById(99L));

        assertEquals("Pharmacy not found: 99", e.getMessage());
    }
}

package com.courcework.controller;

import com.courcework.dto.MedicineDTO;
import com.courcework.service.MedicineService;
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
class MedicineControllerTest {

    @Mock
    private MedicineService medService;

    @InjectMocks
    private MedicineController controller;

    private final MedicineDTO paracetamol = mock(MedicineDTO.class);
    private final MedicineDTO panadol = mock(MedicineDTO.class);

    @Test
    @DisplayName("getAll возвращает список препаратов от сервиса")
    void getAll_returnsServiceResult() {
        when(medService.findAll()).thenReturn(List.of(paracetamol, panadol));

        List<MedicineDTO> result = controller.getAll();

        assertEquals(List.of(paracetamol, panadol), result);
    }

    @Test
    @DisplayName("getAll: препаратов нет, пустой список")
    void getAll_noMedicines_returnsEmptyList() {
        when(medService.findAll()).thenReturn(List.of());

        assertTrue(controller.getAll().isEmpty());
    }

    @Test
    @DisplayName("getById передаёт id сервису и возвращает его результат")
    void getById_returnsServiceResult() {
        when(medService.findById(1L)).thenReturn(paracetamol);

        MedicineDTO result = controller.getById(1L);

        assertEquals(paracetamol, result);
        verify(medService).findById(1L);
    }

    @Test
    @DisplayName("getById: препарат не найден, исключение доходит до обработчика ошибок")
    void getById_notFound_propagatesException() {
        when(medService.findById(99L)).thenThrow(new EntityNotFoundException("Medicine not found: 99"));

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> controller.getById(99L));

        assertEquals("Medicine not found: 99", e.getMessage());
    }

    @Test
    @DisplayName("search передаёт строку поиска сервису без изменений")
    void search_delegatesQueryToService() {
        when(medService.findByName("парац")).thenReturn(List.of(paracetamol));

        List<MedicineDTO> result = controller.search("парац");

        assertEquals(List.of(paracetamol), result);
        verify(medService).findByName("парац");
    }

    @Test
    @DisplayName("search: ничего не найдено, пустой список (не ошибка)")
    void search_noMatches_returnsEmptyList() {
        when(medService.findByName("абвгд")).thenReturn(List.of());

        assertTrue(controller.search("абвгд").isEmpty());
    }

    @Test
    @DisplayName("getAnalogs возвращает аналоги от сервиса")
    void getAnalogs_returnsServiceResult() {
        when(medService.findAnalogs(1L)).thenReturn(List.of(panadol));

        List<MedicineDTO> result = controller.getAnalogs(1L);

        assertEquals(List.of(panadol), result);
        verify(medService).findAnalogs(1L);
    }

    @Test
    @DisplayName("getAnalogs: у препарата нет аналогов, пустой список")
    void getAnalogs_noAnalogs_returnsEmptyList() {
        when(medService.findAnalogs(2L)).thenReturn(List.of());

        assertTrue(controller.getAnalogs(2L).isEmpty());
    }

    @Test
    @DisplayName("getAnalogs: препарат не найден, исключение не скрывается")
    void getAnalogs_unknownMedicine_propagatesException() {
        when(medService.findAnalogs(99L)).thenThrow(new EntityNotFoundException("Medicine not found: 99"));

        assertThrows(EntityNotFoundException.class, () -> controller.getAnalogs(99L));
    }
}

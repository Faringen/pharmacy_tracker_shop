package com.courcework.controller;

import com.courcework.dto.PriceDTO;
import com.courcework.service.PriceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PriceControllerTest {

    @Mock
    private PriceService priceService;

    @InjectMocks
    private PriceController controller;

    private final PriceDTO price = mock(PriceDTO.class);

    @Test
    @DisplayName("Задан только medicineId: цены по препарату")
    void getPrices_onlyMedicineId_returnsPricesByMedicine() {
        when(priceService.findByMedId(1L)).thenReturn(List.of(price));

        List<PriceDTO> result = controller.getPrices(1L, null);

        assertEquals(List.of(price), result);
        verify(priceService, never()).findByPharmId(any());
    }

    @Test
    @DisplayName("Задан только pharmacyId: цены по аптеке")
    void getPrices_onlyPharmacyId_returnsPricesByPharmacy() {
        when(priceService.findByPharmId(2L)).thenReturn(List.of(price));

        List<PriceDTO> result = controller.getPrices(null, 2L);

        assertEquals(List.of(price), result);
        verify(priceService, never()).findByMedId(any());
    }

    @Test
    @DisplayName("Заданы оба параметра: приоритет у medicineId")
    void getPrices_bothParams_medicineIdWins() {
        when(priceService.findByMedId(1L)).thenReturn(List.of(price));

        List<PriceDTO> result = controller.getPrices(1L, 2L);

        assertEquals(List.of(price), result);
        verify(priceService, never()).findByPharmId(any());
    }

    @Test
    @DisplayName("Не задан ни один параметр: IllegalArgumentException, сервис не вызывается")
    void getPrices_noParams_throwsIllegalArgumentException() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> controller.getPrices(null, null));

        assertFalse(e.getMessage().isBlank());
        verifyNoInteractions(priceService);
    }

    @Test
    @DisplayName("У препарата нет цен: пустой список")
    void getPrices_medicineWithoutPrices_returnsEmptyList() {
        when(priceService.findByMedId(5L)).thenReturn(List.of());

        assertTrue(controller.getPrices(5L, null).isEmpty());
    }

    @Test
    @DisplayName("В аптеке нет цен: пустой список")
    void getPrices_pharmacyWithoutPrices_returnsEmptyList() {
        when(priceService.findByPharmId(5L)).thenReturn(List.of());

        assertTrue(controller.getPrices(null, 5L).isEmpty());
    }
}

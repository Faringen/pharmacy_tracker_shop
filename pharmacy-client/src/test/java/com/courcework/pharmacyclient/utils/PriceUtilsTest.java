package com.courcework.pharmacyclient.utils;

import com.courcework.pharmacyclient.dto.PriceDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PriceUtilsTest {

    // из DTO нужна только цена, поэтому используем заглушку вместо реального объекта
    private static PriceDTO priceOf(String value) {
        PriceDTO dto = mock(PriceDTO.class);
        when(dto.price()).thenReturn(new BigDecimal(value));
        return dto;
    }

    // ---------- findMinPrice ----------

    @Test
    @DisplayName("Пустой список: минимальной цены нет")
    void findMinPrice_emptyList_returnsEmpty() {
        assertTrue(PriceUtils.findMinPrice(List.of()).isEmpty());
    }

    @Test
    @DisplayName("Одна цена: она и минимальная")
    void findMinPrice_singlePrice_returnsIt() {
        Optional<BigDecimal> min = PriceUtils.findMinPrice(List.of(priceOf("45.00")));

        assertEquals(0, new BigDecimal("45.00").compareTo(min.orElseThrow()));
    }

    @Test
    @DisplayName("Несколько цен: возвращается наименьшая")
    void findMinPrice_severalPrices_returnsSmallest() {
        List<PriceDTO> prices = List.of(priceOf("210.00"), priceOf("45.00"), priceOf("52.00"), priceOf("49.50"));

        assertEquals(0, new BigDecimal("45.00").compareTo(PriceUtils.findMinPrice(prices).orElseThrow()));
    }

    @Test
    @DisplayName("Несколько одинаковых минимальных цен: значение то же")
    void findMinPrice_equalMinimums_returnsThatValue() {
        List<PriceDTO> prices = List.of(priceOf("45.00"), priceOf("52.00"), priceOf("45.00"));

        assertEquals(0, new BigDecimal("45.00").compareTo(PriceUtils.findMinPrice(prices).orElseThrow()));
    }

    @Test
    @DisplayName("48.0 и 48.00 считаются одним значением")
    void findMinPrice_differentScale_comparedByValue() {
        List<PriceDTO> prices = List.of(priceOf("55"), priceOf("48.0"));

        assertEquals(0, new BigDecimal("48.00").compareTo(PriceUtils.findMinPrice(prices).orElseThrow()));
    }

    // ---------- isMinPrice (подсветка строки таблицы) ----------

    @Test
    @DisplayName("Строка с минимальной ценой подсвечивается")
    void isMinPrice_cheapestRow_returnsTrue() {
        PriceDTO cheapest = priceOf("45.00");
        List<PriceDTO> prices = List.of(cheapest, priceOf("52.00"), priceOf("62.00"));

        assertTrue(PriceUtils.isMinPrice(cheapest, prices));
    }

    @Test
    @DisplayName("Строка с более высокой ценой не подсвечивается")
    void isMinPrice_otherRow_returnsFalse() {
        PriceDTO expensive = priceOf("62.00");
        List<PriceDTO> prices = List.of(priceOf("45.00"), expensive);

        assertFalse(PriceUtils.isMinPrice(expensive, prices));
    }

    @Test
    @DisplayName("Цена 48.0 при минимуме 48.00: подсвечивается (сравнение по значению)")
    void isMinPrice_sameValueDifferentScale_returnsTrue() {
        PriceDTO item = priceOf("48.0");
        List<PriceDTO> prices = List.of(priceOf("48.00"), priceOf("55.00"));

        assertTrue(PriceUtils.isMinPrice(item, prices));
    }

    @Test
    @DisplayName("Пустой список цен: подсвечивать нечего")
    void isMinPrice_emptyList_returnsFalse() {
        assertFalse(PriceUtils.isMinPrice(priceOf("45.00"), List.of()));
    }
}

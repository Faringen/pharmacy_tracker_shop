package com.courcework.pharmacyclient.dto;

import com.courcework.pharmacyclient.api.error.ApiError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Проверка «контракта»: JSON, который отправляет сервер, правильно читается клиентскими DTO.
 * Тесты ловят расхождения в названиях полей (из-за них раньше пропадали названия аптек).
 */
class DtoDeserializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("MedicineDTO читает поля из JSON сервера")
    void medicine_readsFieldsFromServerJson() {
        String json = """
                {"id":1,"name":"Парацетамол","activeSubstance":"Парацетамол","dosageForm":"таблетки",
                 "dosage":"500 мг","manufacturer":null,"description":"Жаропонижающее средство"}""";

        MedicineDTO dto = mapper.readValue(json, MedicineDTO.class);

        assertEquals(1L, dto.id());
        assertEquals("Парацетамол", dto.name());
        assertEquals("500 мг", dto.dosage());
        assertEquals("Жаропонижающее средство", dto.description());
    }

    @Test
    @DisplayName("Лишние поля в JSON игнорируются")
    void medicine_ignoresUnknownFields() {
        String json = "{\"id\":1,\"name\":\"Парацетамол\",\"unknownField\":\"x\"}";

        MedicineDTO dto = mapper.readValue(json, MedicineDTO.class);

        assertEquals("Парацетамол", dto.name());
    }

    @Test
    @DisplayName("Отсутствующие необязательные поля становятся null")
    void medicine_missingFields_areNull() {
        MedicineDTO dto = mapper.readValue("{\"id\":2,\"name\":\"Валидол\"}", MedicineDTO.class);

        assertNull(dto.description());
        assertNull(dto.dosage());
    }

    @Test
    @DisplayName("PriceDTO читает название аптеки, ссылку и цену")
    void price_readsAllFieldsFromServerJson() {
        String json = "{\"medicineName\":\"Парацетамол\",\"pharmacyName\":\"Ригла\","
                + "\"websiteUrl\":\"https://rigla.ru\",\"price\":45.00}";

        PriceDTO dto = mapper.readValue(json, PriceDTO.class);

        assertEquals("Ригла", dto.pharmacyName());
        assertEquals("https://rigla.ru", dto.websiteUrl());
        assertEquals(0, new BigDecimal("45.00").compareTo(dto.price()));
    }

    @Test
    @DisplayName("PharmacyDTO читается без ошибок")
    void pharmacy_readsFromServerJson() {
        String json = "{\"id\":1,\"name\":\"Ригла\",\"address\":\"ул. Ленина, 10\",\"websiteURL\":\"https://rigla.ru\"}";

        assertNotNull(mapper.readValue(json, PharmacyDTO.class));
    }

    @Test
    @DisplayName("ApiError читает тело ошибки сервера (поле timestamp игнорируется)")
    void apiError_readsServerErrorBody() {
        String json = "{\"status\":404,\"error\":\"Not Found\",\"message\":\"Medicine not found: 99\","
                + "\"timestamp\":\"2026-11-14T10:00:00Z\"}";

        ApiError error = mapper.readValue(json, ApiError.class);

        assertEquals(404, error.status());
        assertEquals("Not Found", error.error());
        assertEquals("Medicine not found: 99", error.message());
    }

    @Test
    @DisplayName("ApiError без поля message: message равен null")
    void apiError_withoutMessage_hasNullMessage() {
        ApiError error = mapper.readValue("{\"status\":500,\"error\":\"Internal Server Error\"}", ApiError.class);

        assertNull(error.message());
    }
}

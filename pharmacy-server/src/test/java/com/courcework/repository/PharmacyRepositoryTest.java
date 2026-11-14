package com.courcework.repository;

import com.courcework.entity.Pharmacy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционные тесты PharmacyRepository на встроенной H2.
 * В репозитории нет собственных запросов, поэтому проверяем стандартные операции
 * и ограничения таблицы (уникальный адрес, обязательное название).
 */
@DataJpaTest(properties = "spring.sql.init.mode=never")
class PharmacyRepositoryTest {

    @Autowired
    private PharmacyRepository repository;

    @Test
    @DisplayName("При сохранении аптеке присваивается id")
    void save_assignsId() {
        Pharmacy saved = repository.save(new Pharmacy("Ригла", "ул. Ленина, 10", "https://rigla.ru"));

        assertNotNull(saved.getId());
    }

    @Test
    @DisplayName("Сохранённая аптека находится по id со всеми полями")
    void findById_existing_returnsPharmacyWithAllFields() {
        Pharmacy saved = repository.save(new Pharmacy("Ригла", "ул. Ленина, 10", "https://rigla.ru"));

        Pharmacy found = repository.findById(saved.getId()).orElseThrow();

        assertEquals("Ригла", found.getName());
        assertEquals("ул. Ленина, 10", found.getAddress());
        assertEquals("https://rigla.ru", found.getWebsiteURL());
    }

    @Test
    @DisplayName("Несуществующий id: пустой Optional")
    void findById_unknownId_returnsEmpty() {
        Optional<Pharmacy> found = repository.findById(99999L);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("findAll возвращает все сохранённые аптеки")
    void findAll_returnsAllSavedPharmacies() {
        repository.save(new Pharmacy("Ригла", "ул. Ленина, 10", "https://rigla.ru"));
        repository.save(new Pharmacy("Апрель", "пр. Мира, 25", "https://apteka-april.ru"));

        assertEquals(2, repository.findAll().size());
    }

    @Test
    @DisplayName("Пустая таблица: findAll возвращает пустой список")
    void findAll_emptyTable_returnsEmptyList() {
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Аптека без сайта сохраняется (сайт необязателен)")
    void save_withoutWebsite_isAllowed() {
        Pharmacy saved = repository.saveAndFlush(new Pharmacy("Без сайта", "ул. Новая, 1", null));

        Pharmacy found = repository.findById(saved.getId()).orElseThrow();

        assertNull(found.getWebsiteURL());
    }

    @Test
    @DisplayName("Удаление по id убирает аптеку")
    void deleteById_removesPharmacy() {
        Pharmacy saved = repository.save(new Pharmacy("Ригла", "ул. Ленина, 10", "https://rigla.ru"));

        repository.deleteById(saved.getId());

        assertTrue(repository.findById(saved.getId()).isEmpty());
    }

    // ---------- ограничения таблицы ----------

    @Test
    @DisplayName("Две аптеки с одинаковым адресом не сохраняются")
    void save_duplicateAddress_throwsException() {
        repository.saveAndFlush(new Pharmacy("Ригла", "ул. Ленина, 10", "https://rigla.ru"));
        Pharmacy duplicate = new Pharmacy("Другая", "ул. Ленина, 10", null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(duplicate));
    }

    @Test
    @DisplayName("Аптека без названия не сохраняется")
    void save_withoutName_throwsException() {
        Pharmacy invalid = new Pharmacy(null, "ул. Ленина, 10", null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(invalid));
    }
}

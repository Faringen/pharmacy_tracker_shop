package com.courcework.repository;

import com.courcework.entity.Medicine;
import com.courcework.entity.Pharmacy;
import com.courcework.entity.Price;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Интеграционные тесты PriceRepository на встроенной H2.
 * data.sql отключён, чтобы тесты работали только со своими данными.
 */
@DataJpaTest(properties = "spring.sql.init.mode=never")
class PriceRepositoryTest {

    @Autowired
    private PriceRepository priceRepository;
    @Autowired
    private MedicineRepository medicineRepository;
    @Autowired
    private PharmacyRepository pharmacyRepository;

    @PersistenceContext
    private EntityManager em;

    private Medicine paracetamol;
    private Medicine ibuprofen;
    private Pharmacy rigla;
    private Pharmacy april;

    @BeforeEach
    void setUp() {
        paracetamol = medicineRepository.save(new Medicine("Парацетамол", "Парацетамол", "таблетки", "500 мг"));
        ibuprofen = medicineRepository.save(new Medicine("Ибупрофен", "Ибупрофен", "таблетки", "400 мг"));
        rigla = pharmacyRepository.save(new Pharmacy("Ригла", "ул. Ленина, 10", "https://rigla.ru"));
        april = pharmacyRepository.save(new Pharmacy("Апрель", "пр. Мира, 25", "https://apteka-april.ru"));

        priceRepository.save(new Price(paracetamol, rigla, new BigDecimal("45.00")));
        priceRepository.save(new Price(paracetamol, april, new BigDecimal("52.00")));
        priceRepository.save(new Price(ibuprofen, rigla, new BigDecimal("95.00")));
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("По препарату возвращаются только его цены")
    void findByMedicineId_returnsOnlyPricesOfThatMedicine() {
        List<Price> result = priceRepository.findByMedicineId(paracetamol.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getMedicine().getId().equals(paracetamol.getId())));
    }

    @Test
    @DisplayName("По препарату возвращаются цены из всех аптек, где он продаётся")
    void findByMedicineId_returnsPricesFromAllPharmacies() {
        Set<String> pharmacyNames = priceRepository.findByMedicineId(paracetamol.getId()).stream()
                .map(p -> p.getPharmacy().getName())
                .collect(Collectors.toSet());

        assertEquals(Set.of("Ригла", "Апрель"), pharmacyNames);
    }

    @Test
    @DisplayName("Препарат без цен: пустой список")
    void findByMedicineId_medicineWithoutPrices_returnsEmptyList() {
        Medicine noPrices = medicineRepository.save(new Medicine("Валидол", "Валидол", "таблетки", "60 мг"));

        assertTrue(priceRepository.findByMedicineId(noPrices.getId()).isEmpty());
    }

    @Test
    @DisplayName("Несуществующий id препарата: пустой список")
    void findByMedicineId_unknownId_returnsEmptyList() {
        assertTrue(priceRepository.findByMedicineId(99999L).isEmpty());
    }

    @Test
    @DisplayName("По аптеке возвращаются только её цены")
    void findByPharmacyId_returnsOnlyPricesOfThatPharmacy() {
        List<Price> result = priceRepository.findByPharmacyId(rigla.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getPharmacy().getId().equals(rigla.getId())));
    }

    @Test
    @DisplayName("Аптека без цен: пустой список")
    void findByPharmacyId_pharmacyWithoutPrices_returnsEmptyList() {
        Pharmacy empty = pharmacyRepository.save(new Pharmacy("Пустая", "ул. Новая, 1", null));

        assertTrue(priceRepository.findByPharmacyId(empty.getId()).isEmpty());
    }

    @Test
    @DisplayName("Цена сохраняется без потери точности")
    void save_priceKeepsDecimalPrecision() {
        Price saved = priceRepository.saveAndFlush(new Price(ibuprofen, april, new BigDecimal("49.50")));
        em.clear();

        Price loaded = priceRepository.findById(saved.getId()).orElseThrow();

        assertEquals(0, new BigDecimal("49.50").compareTo(loaded.getPrice()));
    }

    @Test
    @DisplayName("Цена без аптеки не сохраняется")
    void save_withoutPharmacy_throwsException() {
        Price invalid = new Price(paracetamol, null, new BigDecimal("10.00"));

        assertThrows(DataIntegrityViolationException.class, () -> priceRepository.saveAndFlush(invalid));
    }

    @Test
    @DisplayName("Цена без препарата не сохраняется")
    void save_withoutMedicine_throwsException() {
        Price invalid = new Price(null, rigla, new BigDecimal("10.00"));

        assertThrows(DataIntegrityViolationException.class, () -> priceRepository.saveAndFlush(invalid));
    }

    @Test
    @DisplayName("Запись без значения цены не сохраняется")
    void save_withoutPriceValue_throwsException() {
        Price invalid = new Price(paracetamol, rigla, null);

        assertThrows(DataIntegrityViolationException.class, () -> priceRepository.saveAndFlush(invalid));
    }
}

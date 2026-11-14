package com.courcework.service;

import com.courcework.dto.PriceDTO;
import com.courcework.entity.Medicine;
import com.courcework.entity.Pharmacy;
import com.courcework.entity.Price;
import com.courcework.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {
    @Mock
    private PriceRepository repository;

    @InjectMocks
    private PriceService service;

    Price price;
    Medicine medicine;
    Pharmacy pharmacy;

    @BeforeEach
    void setUp(){
        medicine = new Medicine("Test Medicine", "Test Substance", "Dosage","500 g");
        ReflectionTestUtils.setField(medicine, "id", 1L);
        pharmacy = new Pharmacy("Test Pharmacy", "Test Address", "Test UrL");
        ReflectionTestUtils.setField(pharmacy , "id", 4L );
        price = new Price(medicine, pharmacy, new BigDecimal(123));
        ReflectionTestUtils.setField(price, "id", 1L);
    }



    @Test
    void findByPharmId_returnPriceByPharmacyId() {
        when(repository.findByPharmacyId(4L)).thenReturn(List.of(price));

        List<PriceDTO> result = service.findByPharmId(4L);

        assertEquals(1, result.size());
        assertEquals(medicine.getName(), result.get(0).medicineName());
        assertEquals(pharmacy.getName(), result.get(0).pharmacyName());
        assertEquals(price.getPrice(), result.get(0).price());
    }

    @Test
    void findByMedId_returnPriceByMedicineId() {
        when(repository.findByMedicineId(1L)).thenReturn(List.of(price));

        List<PriceDTO> result = service.findByMedId(1L);

        assertEquals(1, result.size());
        assertEquals(medicine.getName(), result.get(0).medicineName());
        assertEquals(pharmacy.getName(), result.get(0).pharmacyName());
        assertEquals(price.getPrice(), result.get(0).price());
    }
}
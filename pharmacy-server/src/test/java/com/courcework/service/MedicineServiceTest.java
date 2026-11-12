package com.courcework.service;

import com.courcework.repository.MedicineRepository;
import jdk.jfr.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {
    @Mock
    private MedicineRepository repository;

    @InjectMocks
    private MedicineService medicineService;

    @Test
    @DisplayName("Тестирование нахождение всех препаратов")
    void findAll() {

    }

    @Test
    void findById() {
    }

    @Test
    void findByName() {
    }

    @Test
    void findAnalogs() {
    }
}
package com.courcework.controller;

import com.courcework.dto.MedicineDTO;
import com.courcework.service.MedicineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест контроллера сервера:")
class MedicineControllerTest {
    @Mock
    private MedicineService service;

    @InjectMocks
    private MedicineController controller;

    private MedicineDTO medicine;
    private List<MedicineDTO> medicineList;

    @BeforeEach
    void setUp(){
        medicine = new MedicineDTO(1L, "testName", "testSubstance",
                "testForm", "testDosage", "testManufacture", "testDescription");
        medicineList = List.of(medicine,
                new MedicineDTO(2L, "testName", "testSubstance",
                        "testForm", "testDosage", "testManufacture", "testDescription"));
    }

   @Nested
    class PositiveTesting{
       @Test
       public void shouldReturnGetAll_getAllTest(){
           int size = 2;
           when(service.findAll()).thenReturn(medicineList);

           List<MedicineDTO> assertList = controller.getAll();


           assertEquals(size, assertList.size());
           assertEquals(medicine.name(), assertList.getFirst().name());
           verify(service).findAll();
       }

       @Test
       public void shouldReturnMedicineById_getByIdTest(){
           Long id = 1L;
           String equalsName = medicine.name();
           when(service.findById(id)).thenReturn(medicine);

           MedicineDTO assertMedicine = controller.getById(id);


           assertNotNull(assertMedicine);
           assertEquals(equalsName, assertMedicine.name());
           verify(service, times(1)).findById(id);
       }

       @Test
       public void shouldReturnMedicineByName_searchTest(){
           List<MedicineDTO> searchList = List.of(medicine);
           String name = medicine.name();

           when(service.findByName(name)).thenReturn(searchList);

           List<MedicineDTO> assertList = controller.search(name);

           assertEquals(name, assertList.getFirst().name());
           assertNotNull(assertList);
           verify(service).findByName(name);
       }
   }

   @Nested
   class NegativeTest{
      @Test
      public void shouldReturnException_findByIdTest(){

       }
   }
}
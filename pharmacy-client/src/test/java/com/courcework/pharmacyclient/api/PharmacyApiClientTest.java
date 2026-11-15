package com.courcework.pharmacyclient.api;

import com.courcework.pharmacyclient.api.error.ApiException;
import com.courcework.pharmacyclient.dto.MedicineDTO;
import com.courcework.pharmacyclient.dto.PriceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Модульные тесты клиента REST API. HttpClient подменён моком, сеть не используется.
 */
@ExtendWith(MockitoExtension.class)
class PharmacyApiClientTest {

    private static final String BASE = "http://localhost:8080/api";

    private static final String TWO_MEDICINES = """
            [{"id":1,"name":"Парацетамол","dosage":"500 мг","description":"d1"},
             {"id":2,"name":"Панадол","dosage":"500 мг","description":"d2"}]""";

    private static final String TWO_PRICES = """
            [{"medicineName":"Парацетамол","pharmacyName":"Ригла","websiteUrl":"https://rigla.ru","price":45.00},
             {"medicineName":"Парацетамол","pharmacyName":"Апрель","websiteUrl":"https://apteka-april.ru","price":52.00}]""";

    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpResponse<String> response;
    @Mock
    private HttpResponse<Void> voidResponse;

    private PharmacyApiClient client;

    @BeforeEach
    void setUp() {
        client = new PharmacyApiClient(httpClient, BASE);
    }

    // ---------- вспомогательные методы ----------

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void stubSend(HttpResponse<?> result) throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn((HttpResponse) result);
    }

    @SuppressWarnings("unchecked")
    private void stubSendThrows(Exception e) throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(e);
    }

    private void respondWith(int status, String body) throws Exception {
        when(response.statusCode()).thenReturn(status);
        when(response.body()).thenReturn(body);
        stubSend(response);
    }

    @SuppressWarnings("unchecked")
    private HttpRequest sentRequest() throws Exception {
        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture(), any(HttpResponse.BodyHandler.class));
        return captor.getValue();
    }

    // ---------- успешные ответы и адреса запросов ----------

    @Test
    @DisplayName("getAllMeds: JSON-массив превращается в список препаратов")
    void getAllMeds_parsesListOfMedicines() throws Exception {
        respondWith(200, TWO_MEDICINES);

        List<MedicineDTO> result = client.getAllMeds();

        assertEquals(2, result.size());
        assertEquals("Парацетамол", result.get(0).name());
        assertEquals("Панадол", result.get(1).name());
        assertEquals(BASE + "/meds", sentRequest().uri().toString());
    }

    @Test
    @DisplayName("getAllMeds: пустой массив даёт пустой список")
    void getAllMeds_emptyArray_returnsEmptyList() throws Exception {
        respondWith(200, "[]");

        assertTrue(client.getAllMeds().isEmpty());
    }

    @Test
    @DisplayName("getMedsByID: объект разбирается, id попадает в адрес")
    void getMedsByID_parsesSingleMedicine() throws Exception {
        respondWith(200, "{\"id\":5,\"name\":\"Ибупрофен\",\"dosage\":\"400 мг\"}");

        MedicineDTO result = client.getMedsByID(5L);

        assertEquals("Ибупрофен", result.name());
        assertEquals(BASE + "/meds/5", sentRequest().uri().toString());
    }

    @Test
    @DisplayName("searchMeds: кириллица и пробел кодируются в адресе")
    void searchMeds_encodesQueryInUrl() throws Exception {
        respondWith(200, "[]");

        client.searchMeds("парац и");

        assertEquals("name=%D0%BF%D0%B0%D1%80%D0%B0%D1%86+%D0%B8", sentRequest().uri().getRawQuery());
    }

    @Test
    @DisplayName("getAnalogs: запрос уходит на /meds/{id}/analogs")
    void getAnalogs_usesAnalogsPath() throws Exception {
        respondWith(200, TWO_MEDICINES);

        List<MedicineDTO> result = client.getAnalogs(3L);

        assertEquals(2, result.size());
        assertEquals(BASE + "/meds/3/analogs", sentRequest().uri().toString());
    }

    @Test
    @DisplayName("getAllPharmacy: список аптек разбирается")
    void getAllPharmacy_parsesList() throws Exception {
        respondWith(200, "[{\"id\":1,\"name\":\"Ригла\"},{\"id\":2,\"name\":\"Апрель\"}]");

        assertEquals(2, client.getAllPharmacy().size());
        assertEquals(BASE + "/pharmacies", sentRequest().uri().toString());
    }

    @Test
    @DisplayName("getPriceByMeds: параметр medicineId, поля цены разбираются")
    void getPriceByMeds_usesMedicineIdParam() throws Exception {
        respondWith(200, TWO_PRICES);

        List<PriceDTO> result = client.getPriceByMeds(1L);

        assertEquals(2, result.size());
        assertEquals("Ригла", result.get(0).pharmacyName());
        assertEquals("https://rigla.ru", result.get(0).websiteUrl());
        assertEquals(0, new BigDecimal("45.00").compareTo(result.get(0).price()));
        assertEquals(BASE + "/price?medicineId=1", sentRequest().uri().toString());
    }

    @Test
    @DisplayName("getPriceByPharmacy: параметр pharmacyId")
    void getPriceByPharmacy_usesPharmacyIdParam() throws Exception {
        respondWith(200, TWO_PRICES);

        client.getPriceByPharmacy(2L);

        assertEquals(BASE + "/price?pharmacyId=2", sentRequest().uri().toString());
    }

    // ---------- обработка ошибок ----------

    @Test
    @DisplayName("404 с JSON: ApiException с кодом и сообщением сервера")
    void notFound_throwsApiExceptionWithServerMessage() throws Exception {
        respondWith(404, "{\"status\":404,\"error\":\"Not Found\",\"message\":\"Medicine not found: 99\"}");

        ApiException e = assertThrows(ApiException.class, () -> client.getMedsByID(99L));

        assertEquals(404, e.getStatusCode());
        assertEquals("Medicine not found: 99", e.getMessage());
    }

    @Test
    @DisplayName("400 с JSON: сообщение сервера доходит до пользователя")
    void badRequest_throwsApiExceptionWithServerMessage() throws Exception {
        respondWith(400, "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Enter a pharmacyID or medicineID.\"}");

        ApiException e = assertThrows(ApiException.class, () -> client.getPriceByMeds(1L));

        assertEquals(400, e.getStatusCode());
        assertEquals("Enter a pharmacyID or medicineID.", e.getMessage());
    }

    @Test
    @DisplayName("Ошибка с телом не в формате JSON: сообщение по умолчанию")
    void errorBodyIsNotJson_usesDefaultMessage() throws Exception {
        respondWith(500, "<html>Internal error</html>");

        ApiException e = assertThrows(ApiException.class, () -> client.getAllMeds());

        assertEquals(500, e.getStatusCode());
        assertEquals("Сервер вернул ошибку 500", e.getMessage());
    }

    @Test
    @DisplayName("Ошибка с пустым message в JSON: сообщение по умолчанию")
    void errorBodyWithBlankMessage_usesDefaultMessage() throws Exception {
        respondWith(500, "{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"\"}");

        ApiException e = assertThrows(ApiException.class, () -> client.getAllMeds());

        assertEquals("Сервер вернул ошибку 500", e.getMessage());
    }

    @Test
    @DisplayName("Сервер недоступен: ApiException с кодом 0")
    void serverUnavailable_throwsApiExceptionWithCodeZero() throws Exception {
        stubSendThrows(new IOException("Connection refused"));

        ApiException e = assertThrows(ApiException.class, () -> client.getAllMeds());

        assertEquals(0, e.getStatusCode());
        assertTrue(e.getMessage().contains("недоступен"));
    }

    @Test
    @DisplayName("Запрос прерван: ApiException с кодом 0, флаг прерывания сохранён")
    void interrupted_throwsApiExceptionAndKeepsInterruptFlag() throws Exception {
        stubSendThrows(new InterruptedException());

        ApiException e = assertThrows(ApiException.class, () -> client.getAllMeds());

        assertEquals(0, e.getStatusCode());
        assertTrue(Thread.interrupted()); // проверяем и заодно сбрасываем флаг, чтобы не влиять на другие тесты
    }

    @Test
    @DisplayName("Успешный ответ с некорректным JSON: должна быть ApiException, а не внутренняя ошибка Jackson")
    void malformedJsonInSuccessResponse_throwsApiException() throws Exception {
        respondWith(200, "это не JSON");

        assertThrows(ApiException.class, () -> client.getAllMeds());
    }

    // ---------- ожидание запуска сервера ----------

    @Test
    @DisplayName("waitingForServer: сервер отвечает сразу, одна попытка")
    void waitingForServer_serverUp_returnsTrueAfterOneAttempt() throws Exception {
        when(voidResponse.statusCode()).thenReturn(200);
        stubSend(voidResponse);

        assertTrue(client.waitingForServer(5, 0));

        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    @DisplayName("waitingForServer: код 404 тоже означает, что сервер поднялся")
    void waitingForServer_notFound_meansServerIsUp() throws Exception {
        when(voidResponse.statusCode()).thenReturn(404);
        stubSend(voidResponse);

        assertTrue(client.waitingForServer(5, 0));
    }

    @Test
    @DisplayName("waitingForServer: сначала 503, потом 200, успех со второй попытки")
    void waitingForServer_serverErrorThenOk_retries() throws Exception {
        when(voidResponse.statusCode()).thenReturn(503, 200);
        stubSend(voidResponse);

        assertTrue(client.waitingForServer(5, 0));

        verify(httpClient, times(2)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    @DisplayName("waitingForServer: сервер не отвечает, false после всех попыток")
    void waitingForServer_connectionRefused_returnsFalseAfterAllAttempts() throws Exception {
        stubSendThrows(new IOException("Connection refused"));

        assertFalse(client.waitingForServer(3, 0));

        verify(httpClient, times(3)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    @DisplayName("waitingForServer: сервер всё время отвечает 500, false")
    void waitingForServer_alwaysServerError_returnsFalse() throws Exception {
        when(voidResponse.statusCode()).thenReturn(500);
        stubSend(voidResponse);

        assertFalse(client.waitingForServer(2, 0));

        verify(httpClient, times(2)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }
}

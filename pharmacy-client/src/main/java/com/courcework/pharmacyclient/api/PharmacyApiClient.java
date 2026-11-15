package com.courcework.pharmacyclient.api;

import com.courcework.pharmacyclient.api.error.*;
import com.courcework.pharmacyclient.dto.MedicineDTO;
import com.courcework.pharmacyclient.dto.PharmacyDTO;
import com.courcework.pharmacyclient.dto.PriceDTO;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class PharmacyApiClient {
    private static final String DEFAULT_URL = "http://localhost:8080/api";

    private final HttpClient httpClient;
    private final String baseUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public PharmacyApiClient() {
        this(HttpClient.newHttpClient(), DEFAULT_URL);
    }

    public PharmacyApiClient(HttpClient httpClient, String baseUrl) {
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
    }

    public List<MedicineDTO> getAllMeds(){
        return getList("/meds", MedicineDTO.class);
    }

    public MedicineDTO getMedsByID(Long id){
        return get("/meds/" + id, MedicineDTO.class);
    }

    public List<MedicineDTO> searchMeds(String name){
        return getList("/meds/search?name=" + encode(name), MedicineDTO.class);
    }

    public List<MedicineDTO> getAnalogs(Long medsID){
        return getList("/meds/" + medsID + "/analogs", MedicineDTO.class);
    }

    public List<PharmacyDTO> getAllPharmacy(){
        return getList("/pharmacies", PharmacyDTO.class);
    }

    public PharmacyDTO getPharmacyByID (Long id){
        return get("/pharmacies/" + id, PharmacyDTO.class);
    }

    public List<PriceDTO> getPriceByMeds(Long medID){
        return getList("/price?medicineId=" + medID, PriceDTO.class);
    }

    public List<PriceDTO> getPriceByPharmacy(Long pharmacyID){
        return getList("/price?pharmacyId=" + pharmacyID, PriceDTO.class);
    }

    private <T> List<T> getList(String path, Class<T> itemType){
        JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, itemType);
        return send(path, type);
    }

    private <T> T get(String path, Class<T> type){
        return send(path, mapper.getTypeFactory().constructType(type));
    }

    private <T> T send(String path, JavaType type){
        HttpResponse<String> response;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path)).timeout(Duration.ofSeconds(5))
                    .GET().build();
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new ApiException(0, "Запрос был прерван");
        }catch (IOException e){
            throw new ApiException(0, "Сервер недоступен. Проверьте что он запущен");
        }

        if(response.statusCode() >=400) throw new ApiException(response.statusCode(), extractMessage(response));
        try {
            return mapper.readValue(response.body(), type);
        } catch (RuntimeException e) {
            throw new ApiException(response.statusCode(), "Некорректный ответ сервера");
        }
    }

    private String extractMessage(HttpResponse<String> response) {
        try {
            ApiError error = mapper.readValue(response.body(), ApiError.class);
            if(error.message() != null && !error.message().isBlank()) return error.message();
        }catch (RuntimeException e){

        }
        return "Сервер вернул ошибку " + response.statusCode();
    }

    public boolean waitingForServer(int maxAttempts, long delayMillis) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/pharmacies"))
                .GET().build();
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
                if (response.statusCode() < 500) return true;
            } catch (Exception e){

            }
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }

        }
        return false;
    }


    private String encode(String value){
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

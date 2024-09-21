package com.example.integratebank.bank.client;

import com.example.integratebank.dto.SCBInquiryRequestDTO;
import com.example.integratebank.dto.SCBInquiryResponseDTO;
import com.example.integratebank.dto.ScbBankRequestDTO;
import com.example.integratebank.dto.ScbPurchaseResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class SCBClient {
    private final String apikey;
    private final String secretKey;
    private final OkHttpClient client;
    private final ObjectMapper jsonMapper;


    public SCBClient(@Value("${scb.api.key}") String apikey,
                     @Value("${scb.secret.key}") String secretKey,
                     OkHttpClient okHttpClient) {
        this.apikey = apikey;
        this.secretKey = secretKey;
        this.client = okHttpClient;
        jsonMapper = new Jackson2ObjectMapperBuilder().build();
        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    /**
     * call api confirm payment of SCB
     * @param url String
     * @param scbBankRequestDTO ScbBankRequestDTO
     * @return Optional<ScbPurchaseResponseDTO>
     */
    public Optional<ScbPurchaseResponseDTO> confirmPayment(String url, ScbBankRequestDTO scbBankRequestDTO) {
        return request(url, scbBankRequestDTO)
                .map(response -> {
                    try {
                        return jsonMapper.readValue(response, ScbPurchaseResponseDTO.class);
                    } catch (JsonProcessingException e) {
                        log.error("Can't get payment status from SCB", e);
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * Call api get inquiry
     * @param url String
     * @param scbInquiryRequestDTO SCBInquiryRequestDTO
     * @return Optional<SCBInquiryResponseDTO>
     */
    public Optional<SCBInquiryResponseDTO> getTransactionInquiry(String url, SCBInquiryRequestDTO scbInquiryRequestDTO) {
        return request(url, scbInquiryRequestDTO)
                .map(response -> {
                    try {
                        return jsonMapper.readValue(response, SCBInquiryResponseDTO.class);
                    } catch (JsonProcessingException e) {
                        log.error("Can't get payment status from Pay Solutions", e);
                        throw new RuntimeException(e);
                    }
                });
    }


    /**
     * Custom request and get response
     * @param url String
     * @param dto generic
     * @return Optional<String>
     */
    private <T> Optional<String> request(String url, T dto) {
        Request request = buildPostRequest(url, dto);

        log.info("SCB request: {}", request);
        try (Response response = client.newCall(request).execute()) {
            final int code = response.code();
            final ResponseBody body = response.body();
            final String bodyString = body != null ? body.string() : null;

            if (code == 200) {
                return Optional.ofNullable(bodyString);
            }

            log.info("SCB response code: {}, body: {}", code, bodyString);
            return Optional.empty();
        } catch (IOException e) {
            log.error("Can't get response from SCB", e);
            return Optional.empty();
        }
    }

    /**
     * Build post request
     * @param url String
     * @param dto generic
     * @return Request
     */
    private <T> Request buildPostRequest(String url, T dto) {
        try {
            String bodyJson = jsonMapper.writeValueAsString(dto);
            RequestBody requestBody = RequestBody.create(bodyJson, MediaType.get("application/json; charset=utf-8"));

            // set header following documents
            return new Request.Builder()
                    .url(url)
                    .header("Content-Type", "application/json")
                    .header("merchantSecretKey", secretKey)
                    .header("apikey", apikey)
                    .post(requestBody)
                    .build();
        } catch (Exception e) {
            log.error("Can't parse request body to json", e);
            throw new RuntimeException(e);
        }
    }
}

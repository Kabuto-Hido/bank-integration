package com.example.integratebank.bank.client;

import com.example.integratebank.dto.KBANKInquiryResponseDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KBANKClient {
    private final String loginId;
    private final String password;
    private final String defaultMerchantId;
    private final OkHttpClient client;
    private final ObjectMapper jsonMapper;
    private final String inquiryUrl;

    public KBANKClient(@Value("${kbank.login.id}") String loginId,
                       @Value("${kbank.password}")String password,
                       @Value("${kbank.merchant.id}") String defaultMerchantId,
                       OkHttpClient okHttpClient,
                       @Value("${kbank.inquiry.url}") String inquiryUrl) {
        this.loginId = loginId;
        this.password = password;
        this.defaultMerchantId = defaultMerchantId;
        this.client = okHttpClient;
        this.inquiryUrl = inquiryUrl;
        jsonMapper = new Jackson2ObjectMapperBuilder().build();
        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    public Optional<KBANKInquiryResponseDTO> getPaymentStatus(String refNo) {
        Map<String, String> params = new HashMap<>();
        if (refNo != null) {
            params.put("refNo", refNo);
        }
        log.info("KBANKQuery request: {}", params.entrySet().stream()
                                               .map(entry -> entry.getKey() + "=" + entry.getValue())
                                               .collect(Collectors.joining(", ", "{", "}")));
        return request(inquiryUrl, params)
                .map(response -> {
                    log.info("KBANK response: {}", response);
                    try {
                        return jsonMapper.readValue(response, KBANKInquiryResponseDTO.class);
                    } catch (IOException e) {
                        log.error("Can't parse KBANK response", e);
                        return null;
                    }
                });
    }


    /**
     * Build request to bank
     * @param url  String
     * @param params Map<String, String>
     * @return Optional<String>
     */
    private Optional<String> request(String url, Map<String, String> params) {
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("merchantId", defaultMerchantId)
                .add("loginId", loginId)
                .add("password", password);
        params.forEach(formBuilder::add);
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Charset", "utf-8")
                .post(formBuilder.build())
                .build();
        try (Response response = client.newCall(request).execute()) {
            final int code = response.code();
            final ResponseBody body = response.body();
            final String bodyString = body != null ? body.string() : null;
            if (code == 200) {
                return Optional.ofNullable(bodyString);
            }
            log.error("KBANK response code: {}, body: {}", code, bodyString);
            return Optional.empty();
        } catch (IOException e) {
            log.error("Can't get response from KBANK", e);
            return Optional.empty();
        }
    }

}

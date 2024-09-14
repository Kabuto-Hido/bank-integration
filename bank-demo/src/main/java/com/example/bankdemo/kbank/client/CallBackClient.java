package com.example.bankdemo.kbank.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class CallBackClient {

    private final OkHttpClient client;
    private final ObjectMapper jsonMapper;

    public CallBackClient(OkHttpClient client) {
        this.client = client;
        jsonMapper = new Jackson2ObjectMapperBuilder().build();
        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    public void callbackDatafeed(String url, Map<String, String> params) {
        postRequest(url, null, params);
    }

    private <T> Optional<String> postRequest(String url, T dto, Map<String, String> params) {
        Request request = buildPostRequestWithParam(url, dto, params);

        try (Response response = client.newCall(request).execute()) {
            final ResponseBody body = response.body();
            final String bodyString = body != null ? body.string() : null;

            // Check if body is empty or null
            if (bodyString == null || bodyString.trim().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(bodyString);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private <T> Request buildPostRequestWithParam(String url, T dto, Map<String, String> params) {
        try {
            HttpUrl.Builder urlBuilder = buildUrlWithParams(url, params);
            String finalUrl = urlBuilder.build().toString();
            String bodyJson = jsonMapper.writeValueAsString(dto);
            RequestBody requestBody = RequestBody.create(bodyJson, MediaType.get("application/json; charset=utf-8"));

            // set header following documents
            return new Request.Builder()
                    .url(finalUrl)
                    .header("Content-Type", "application/json")
                    .post(requestBody)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static HttpUrl.Builder buildUrlWithParams(String url, Map<String, String> params) {
        HttpUrl httpUrl  = HttpUrl.parse(url);
        if (httpUrl == null) {
            throw new RuntimeException("Invalid URL");
        }
        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        if (params != null && !params.isEmpty()) {
            params.forEach(urlBuilder::addQueryParameter);
        }
        return urlBuilder;
    }
}

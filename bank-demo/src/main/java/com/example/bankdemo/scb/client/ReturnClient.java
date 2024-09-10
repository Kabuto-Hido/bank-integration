package com.example.bankdemo.scb.client;

import com.example.bankdemo.scb.dto.ReturnDataFeedDTO;
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
public class ReturnClient {
    private final OkHttpClient client;
    private final ObjectMapper jsonMapper;

    public ReturnClient(OkHttpClient client) {
        this.client = client;
        jsonMapper = new Jackson2ObjectMapperBuilder().build();
        jsonMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    public void returnDataFeed(String url, ReturnDataFeedDTO dto) {
        postRequest(url, dto);
    }

    public void callFeReturnURL(String url, Map<String, String> params) {
        getRequest(url, params);
    }

    private <T> Optional<String> postRequest(String url, T dto) {
        Request request = buildPostRequest(url, dto);

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

    private Optional<String> getRequest(String url, Map<String, String> params) {
        Request request = buildGetRequest(url, params);

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

    private <T> Request buildPostRequest(String url, T dto) {
        try {
            String bodyJson = jsonMapper.writeValueAsString(dto);
            RequestBody requestBody = RequestBody.create(bodyJson, MediaType.get("application/json; charset=utf-8"));

            // set header following documents
            return new Request.Builder()
                    .url(url)
                    .header("Content-Type", "application/json")
                    .post(requestBody)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Request buildGetRequest(String url, Map<String, String> params) {
        try {

            HttpUrl.Builder urlBuilder = buildUrlWithParams(url, params);
            String finalUrl = urlBuilder.build().toString();

            // set header following documents
            return new Request.Builder()
                    .url(finalUrl)
                    .header("Content-Type", "application/json")
                    .get()
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

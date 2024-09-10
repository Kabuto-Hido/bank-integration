package com.example.integratebank.util;

import com.example.integratebank.exception.BadRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class PaymentUtil {

    private final AESHelper aesHelper;

    /**
     * Encrypt object
     * @param data T
     * @param message String
     * @return String
     */
    public <T> String encryptObject(T data, String message) {
        try {
            ObjectMapper mapper = new Jackson2ObjectMapperBuilder().build();
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            String jsonData = mapper.writeValueAsString(data);
            return aesHelper.encrypt(jsonData);
        } catch (JsonProcessingException e) {
            throw new BadRequest(message);
        }
    }

    /**
     * Formats the given LocalDateTime into a string with the pattern "yyyyMMddHHmmssSSS".
     * @param date LocalDateTime
     * @return String
     */
    public String getCurrentDateFormatted(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return date.format(formatter);
    }

    /**
     * Parses a date string in ISO format and formats it into a string with the pattern "yyyyMMddHHmmssSSS".
     * @param date String
     * @return String
     */
    public String getCurrentDateFormatted(String date) {
        LocalDateTime parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        return getCurrentDateFormatted(parsedDate);
    }

    /**
     * Generates a customer ID in the format "CUST" followed by a random 4-digit number.
     * The number is generated based on the current epoch time.
     * @return String
     */
    public String generateCustomerId() {
        long currentTimeMillis = Instant.now().toEpochMilli();
        int randomNumber = (int) (currentTimeMillis % 9000) + 1000;
        return "CUST" + randomNumber;
    }
}

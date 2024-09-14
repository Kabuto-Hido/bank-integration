package com.example.integratebank.util;

import com.example.integratebank.exception.BadRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class PaymentUtil {

    private final AESHelper aesHelper;

    public static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    public static final int SCALE = 2;

    private static final ThreadLocal<DecimalFormatSymbols> DECIMAL_FORMAT_SYMBOLS =
            ThreadLocal.withInitial(() -> DecimalFormatSymbols.getInstance(Locale.ENGLISH));

    private static final ThreadLocal<DecimalFormat> DECIMAL_FORMATTER =
            ThreadLocal.withInitial(() -> new DecimalFormat("0.00", DECIMAL_FORMAT_SYMBOLS.get()));

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

    public static String formatDecimal(@Nonnull BigDecimal d) {
        return DECIMAL_FORMATTER.get().format(setMSScale(d));
    }

    public static BigDecimal setMSScale(int scale, BigDecimal d) {
        return d.setScale(scale, MC.getRoundingMode());
    }

    public static BigDecimal setMSScale(BigDecimal d) {
        return setMSScale(SCALE, d);
    }
}

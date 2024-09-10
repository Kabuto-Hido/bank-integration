package com.example.integratebank.datafeed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class converts a Map<String, Object> to a JSON string
 * and converts a JSON string back to a Map<String, Object> for entity attribute mapping.
 */
@Converter
class DataFeedResponseConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final Logger log = LoggerFactory.getLogger(DataFeedResponseConverter.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Converts a Map<String, Object> to its JSON string.
     * @param value Map<String, Object>
     * @return String
     */
    @Override
    public String convertToDatabaseColumn(Map<String, Object> value) {
        String mappedValue = "";
        if (value != null) {
            try {
                mappedValue = mapper.writeValueAsString(value);
            } catch (IOException e) {
                log.error("Can't serialize map property", e);
            }
        }
        return mappedValue;
    }

    /**
     * Converts a JSON string from the database into a Map<String, Object>.
     * @param value JSON String
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> convertToEntityAttribute(String value) {
        Map<String, Object> mappedValue = Collections.emptyMap();
        final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
        if (value != null) {
            try {
                mappedValue = mapper.readValue(value, typeRef);
            } catch (IOException e) {
                log.error("Can't deserialize string to map", e);
            }
        }
        return mappedValue;
    }
}

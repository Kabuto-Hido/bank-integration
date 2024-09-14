package com.example.integratebank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KBANKInquiryResponseDTO {
    private KBANKStatus status;
    private String ref;
    private String payRef;
    private String currency;
    private BigDecimal baseAmount;
    private BigDecimal totalAmount;

    public enum KBANKStatus {
        PENDING,
        FAIL,
        VOID,
        SUCCESS,
        CANCEL
    }
}

package com.example.bankdemo.kbank.dto;

import com.example.bankdemo.kbank.enumration.KBANKStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class KBANKInquiryDTO {
    private KBANKStatus status;
    private String ref;
    private String payRef;
    private String currency;
    private BigDecimal baseAmount;
    private BigDecimal totalAmount;
}

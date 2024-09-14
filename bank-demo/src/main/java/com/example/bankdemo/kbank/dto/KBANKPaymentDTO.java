package com.example.bankdemo.kbank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KBANKPaymentDTO {
    private String refNo;
    private String merchantId;
    private String customerEmail;
    private String currency;
    private BigDecimal amount;
    private String channel;
    private String returnUrl;

    // card info
    private String cardName;
    private String cardNumber;
    private String yearExpiry;
    private String monthExpiry;
    private String cvv;
}

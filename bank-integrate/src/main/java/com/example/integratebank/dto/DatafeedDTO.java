package com.example.integratebank.dto;

import com.example.integratebank.enumeration.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatafeedDTO {
    private String transactionId;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private Date dateOfCreation;
    private Map<String, Object> response;
    private String providerReference;
}

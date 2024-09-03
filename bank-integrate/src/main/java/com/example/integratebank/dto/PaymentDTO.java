package com.example.integratebank.dto;

import com.example.integratebank.enumeration.PaymentProvider;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDTO {
    private BigDecimal amount;
    private PaymentProvider provider;

    // Customer information
    private String firstname;
    private String lastname;
    private String email;
    private String message;
}

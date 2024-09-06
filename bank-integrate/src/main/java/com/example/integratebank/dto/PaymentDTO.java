package com.example.integratebank.dto;

import com.example.integratebank.enumeration.PaymentProvider;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "1.0", inclusive = false, message = "Amount must be lower than one")
    @Digits(integer = 10, fraction = 2, message = "Amount must be a number with up to 10 digits and up to 2 decimal places.")
    private BigDecimal amount;

    @NotNull(message = "Bank provider can not be null")
    private PaymentProvider provider;

    // Customer information
    @NotEmpty(message = "First name cannot empty")
    private String firstname;
    @NotEmpty(message = "Last name cannot empty")
    private String lastname;
    @Email
    private String email;
    private String message;
}

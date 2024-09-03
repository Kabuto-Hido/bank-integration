package com.example.integratebank.dto;

import com.example.integratebank.enumeration.PaymentProvider;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardInfoRequestDTO {

    @NotNull(message = "Bank provider can not be null")
    private PaymentProvider provider;

    @NotEmpty(message = "Transaction id can not be empty")
    private String transactionId;

    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNo;

    @Size(min = 3, max = 4, message = "CVV must be 3 or 4 digits")
    private String securityCode;

    @Pattern(regexp = "(0[1-9]|1[0-2])", message = "Expiration month must be in MM format")
    private String epMonth;

    @Pattern(regexp = "\\d{4}", message = "Expiration year must be in YYYY format")
    private String epYear;
}

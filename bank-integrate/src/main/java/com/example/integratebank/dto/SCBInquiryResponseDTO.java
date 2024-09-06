package com.example.integratebank.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SCBInquiryResponseDTO {
    private ScbPurchaseResponseDTO.BankStatus status;
    private InquiryData data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InquiryData {
        private String merchantId;
        private String txnNumber;
        private String statusCode;
        private String statusDesc;
        private BigDecimal amount;
    }
}

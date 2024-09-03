package com.example.integratebank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Following the document from the bank to define DTO
 * to do payment
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScbBankRequestDTO {
    private String merchantId;
    private PaymentInfo paymentInfo;
    private IppInfo ippInfo;
    private List<OtherInfo> otherInfo;
    private String cardInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentInfo {
        private String orderNumber;
        private String orderDateTime;
        private String currency;
        private BigDecimal amount;
        private String customerId;
        private String settlementMode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IppInfo {
        private Integer ippType;
        private String ippPlanName;
        private Integer ippMonth;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OtherInfo {
        private String key;
        private String value;
    }
}

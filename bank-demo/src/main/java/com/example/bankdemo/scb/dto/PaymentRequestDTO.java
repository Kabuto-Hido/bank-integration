package com.example.bankdemo.scb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequestDTO {
    private String merchantId;
    private PaymentInfo paymentInfo;
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
    public static class OtherInfo {
        private String key;
        private String value;
    }
}

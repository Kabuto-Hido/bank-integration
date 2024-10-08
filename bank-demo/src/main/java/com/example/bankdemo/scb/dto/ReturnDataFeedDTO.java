package com.example.bankdemo.scb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReturnDataFeedDTO {
    private DataResponse data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataResponse {
        private String merchantId;
        private AuthorizeResponse authorizeResponse;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorizeResponse {
        private String txnNumber;
        private String statusCode;
        private String statusDesc;
        private String orderNumber;
        private List<PaymentRequestDTO.OtherInfo> otherInfo;
    }
}

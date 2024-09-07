package com.example.bankdemo.scb.payment;

import com.example.bankdemo.exception.NotFoundException;
import com.example.bankdemo.scb.dto.PaymentInquiryRequestDTO;
import com.example.bankdemo.scb.dto.PaymentInquiryResponseDTO;
import com.example.bankdemo.scb.dto.PaymentRequestDTO;
import com.example.bankdemo.scb.dto.PaymentResponseDTO;
import com.example.bankdemo.scb.enumeration.SCBStatus;
import com.example.bankdemo.scb.merchant.SCBMerchant;
import com.example.bankdemo.scb.merchant.SCBMerchantService;
import com.example.bankdemo.scb.order.SCBOrder;
import com.example.bankdemo.scb.order.SCBOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final SCBMerchantService scbMerchantService;

    private final SCBOrderService scbOrderService;

    public final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    private final DateTimeFormatter FORMATTER_TRANSACTION_ID_DATETIME =
            DateTimeFormatter.ofPattern("ddMMyyHHmmss");

    @Override
    public PaymentResponseDTO doPayment(PaymentRequestDTO paymentRequestDTO, String apiKey, String secretKey) {
        SCBMerchant scbMerchant = scbMerchantService.findByMerchantId(paymentRequestDTO.getMerchantId())
                .orElseThrow(() -> new NotFoundException("Invalid merchant id"));

        authorizationPayment(apiKey, secretKey, scbMerchant);

        // calculate paid amount
        BigDecimal fee = scbMerchant.getFee();
        BigDecimal feeValue = paymentRequestDTO.getPaymentInfo().getAmount()
                                             .multiply(fee).divide(ONE_HUNDRED, MC);
        BigDecimal amount = paymentRequestDTO.getPaymentInfo().getAmount().add(feeValue, MC);

        SCBOrder scbOrder = scbOrderService.save(new SCBOrder(
                paymentRequestDTO.getPaymentInfo().getOrderNumber(),
                generatePrefixTrx(),
                amount,
                paymentRequestDTO.getPaymentInfo().getCustomerId(),
                SCBStatus.SUCCESS,
                "1001",
                scbMerchant
        ));

        //init response
        PaymentResponseDTO.BankStatus bankStatus = PaymentResponseDTO.BankStatus.builder()
                                                                                .code(1000)
                                                                                .description("Success")
                                                                                .build();

        PaymentResponseDTO.AuthorizeResponse authorizeResponse = new PaymentResponseDTO.AuthorizeResponse(
                scbOrder.getTransactionId(),
                "1000",
                SCBStatus.SUCCESS.name(),
                Collections.emptyList()
        );

        PaymentResponseDTO.DataResponse dataResponse = new PaymentResponseDTO.DataResponse(
                scbMerchant.getMerchantId(),
                authorizeResponse
        );

        return PaymentResponseDTO.builder().status(bankStatus).data(dataResponse).build();
    }

    @Override
    public PaymentInquiryResponseDTO getInquiry(PaymentInquiryRequestDTO paymentInquiryRequestDTO, String apiKey,
                                                String secretKey) {
        SCBMerchant scbMerchant = scbMerchantService.findByMerchantId(paymentInquiryRequestDTO.getMerchantId())
                                                    .orElseThrow(() -> new NotFoundException("Invalid merchant id"));

        authorizationPayment(apiKey, secretKey, scbMerchant);
        SCBOrder scbOrder = scbOrderService.findByOrderNoAndMerchantId(paymentInquiryRequestDTO.getOrderNumber(),
                                                                       paymentInquiryRequestDTO.getMerchantId())
                                           .orElseThrow(() -> new NotFoundException("Not found transaction"));

        PaymentResponseDTO.BankStatus bankStatus = PaymentResponseDTO.BankStatus.builder()
                                                                                .code(1000)
                                                                                .description("Success")
                                                                                .build();
        PaymentInquiryResponseDTO.InquiryData inquiryData = PaymentInquiryResponseDTO.InquiryData.builder()
                .amount(scbOrder.getAmount())
                .statusCode(scbOrder.getStatusCode())
                .statusDesc(scbOrder.getStatus().name())
                .txnNumber(scbOrder.getTransactionId())
                .merchantId(scbMerchant.getMerchantId()).build();

        return PaymentInquiryResponseDTO.builder().status(bankStatus).data(inquiryData).build();
    }

    private void authorizationPayment(String apiKey, String secretKey, SCBMerchant scbMerchant) {
        if (!scbMerchant.getApiKey().equals(apiKey) ||
                !scbMerchant.getSecretKey().equals(secretKey)) {
            throw new NotFoundException("Invalid key");
        }
    }

    private String generatePrefixTrx() {
        return "SC" + LocalDateTime.now().format(FORMATTER_TRANSACTION_ID_DATETIME) + "B";
    }
}

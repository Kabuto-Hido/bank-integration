package com.example.bankdemo.scb.payment;

import com.example.bankdemo.exception.AuthException;
import com.example.bankdemo.exception.NotFoundException;
import com.example.bankdemo.scb.client.ReturnClient;
import com.example.bankdemo.scb.dto.PaymentInquiryRequestDTO;
import com.example.bankdemo.scb.dto.PaymentInquiryResponseDTO;
import com.example.bankdemo.scb.dto.PaymentRequestDTO;
import com.example.bankdemo.scb.dto.PaymentResponseDTO;
import com.example.bankdemo.scb.dto.ReturnDataFeedDTO;
import com.example.bankdemo.scb.enumeration.SCBStatus;
import com.example.bankdemo.scb.merchant.SCBMerchant;
import com.example.bankdemo.scb.merchant.SCBMerchantService;
import com.example.bankdemo.scb.order.SCBOrder;
import com.example.bankdemo.scb.order.SCBOrderService;
import com.example.bankdemo.scb.util.AESHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final SCBMerchantService scbMerchantService;

    private final SCBOrderService scbOrderService;

    private final AESHelper aesHelper;

    private final ReturnClient client;

    public final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    private final DateTimeFormatter FORMATTER_TRANSACTION_ID_DATETIME =
            DateTimeFormatter.ofPattern("ddMMyyHHmmss");

    @Value("#{${status.map}}")
    private Map<SCBStatus, String> statusMap;

    @Override
    public PaymentResponseDTO doPayment(PaymentRequestDTO paymentRequestDTO, String apiKey, String secretKey) {
        SCBMerchant scbMerchant = scbMerchantService.findByMerchantId(paymentRequestDTO.getMerchantId())
                .orElseThrow(() -> new AuthException("Invalid merchant id"));

        authorizationPayment(apiKey, secretKey, scbMerchant);

        // calculate paid amount
        BigDecimal fee = scbMerchant.getFee();
        BigDecimal feeValue = paymentRequestDTO.getPaymentInfo().getAmount()
                                             .multiply(fee).divide(ONE_HUNDRED, MC);
        BigDecimal amount = paymentRequestDTO.getPaymentInfo().getAmount().add(feeValue, MC);

        // decrypt card info
        HashMap<String, String> cardInfo = aesHelper.decryptObject(paymentRequestDTO.getCardInfo(),
                                                                   new TypeReference<>() {
                                                                   });

        SCBStatus scbStatus = SCBStatus.PENDING;
        if (cardInfo.containsKey("cardNumber")) {
            String cardNumber = cardInfo.get("cardNumber");
            if (cardNumber.startsWith("356")) {
                scbStatus = SCBStatus.SUCCESS;
            }
            else if (cardNumber.startsWith("456")) {
                scbStatus = SCBStatus.FAIL;
            }
        }
        String statusCode = statusMap.get(scbStatus);
        SCBOrder scbOrder = scbOrderService.save(new SCBOrder(
                paymentRequestDTO.getPaymentInfo().getOrderNumber(),
                generatePrefixTrx(),
                amount,
                paymentRequestDTO.getPaymentInfo().getCustomerId(),
                scbStatus,
                statusCode,
                scbMerchant
        ));

        // callback datafeed to merchant
        callbackDataFeed(paymentRequestDTO, scbMerchant, scbOrder, statusCode, scbStatus);

        //init response
        PaymentResponseDTO.BankStatus bankStatus = PaymentResponseDTO.BankStatus.builder()
                                                                                .code(1000)
                                                                                .description("Success")
                                                                                .build();

        PaymentResponseDTO.AuthorizeResponse authorizeResponse = new PaymentResponseDTO.AuthorizeResponse(
                scbOrder.getTransactionId(),
                statusCode,
                scbStatus.name(),
                Collections.emptyList()
        );

        PaymentResponseDTO.DataResponse dataResponse = new PaymentResponseDTO.DataResponse(
                scbMerchant.getMerchantId(),
                authorizeResponse
        );

        return PaymentResponseDTO.builder().status(bankStatus).data(dataResponse).build();
    }

    private void callbackDataFeed(PaymentRequestDTO paymentRequestDTO, SCBMerchant scbMerchant, SCBOrder scbOrder,
                           String statusCode, SCBStatus scbStatus) {
        String backendReturnUrlValue = paymentRequestDTO.getOtherInfo().stream()
                                                        .filter(info -> "backendReturnUrl".equals(info.getKey()))
                                                        .map(PaymentRequestDTO.OtherInfo::getValue)
                                                        .findFirst()
                                                        .orElse(scbMerchant.getBeReturnURL());
        if (backendReturnUrlValue == null) {
            return;
        }

        ReturnDataFeedDTO.AuthorizeResponse returnResponse = new ReturnDataFeedDTO.AuthorizeResponse(scbOrder.getTransactionId(),
                                                                                                     statusCode,
                                                                                                     scbStatus.name(),
                                                                                                     scbOrder.getOrderNo(),
                                                                                                     Collections.emptyList());
        ReturnDataFeedDTO.DataResponse responseData = new ReturnDataFeedDTO.DataResponse(scbMerchant.getMerchantId(), returnResponse);
        ReturnDataFeedDTO returnDataFeedDTO = new ReturnDataFeedDTO(responseData);
        client.returnDataFeed(backendReturnUrlValue, returnDataFeedDTO);
    }

    @Override
    public void redirectFEUrl(PaymentRequestDTO paymentRequestDTO,
                                  String statusCode) {
        SCBMerchant scbMerchant = scbMerchantService.findByMerchantId(paymentRequestDTO.getMerchantId())
                                                    .orElseThrow(() -> new AuthException("Invalid merchant id"));

        SCBOrder scbOrder = scbOrderService.findByOrderNoAndMerchantId(paymentRequestDTO.getPaymentInfo().getOrderNumber(),
                                                                       paymentRequestDTO.getMerchantId())
                                           .orElseThrow(() -> new AuthException("Invalid order number"));

        String frontendReturnUrlValue = paymentRequestDTO.getOtherInfo().stream()
                                                        .filter(info -> "frontendReturnUrl".equals(info.getKey()))
                                                        .map(PaymentRequestDTO.OtherInfo::getValue)
                                                        .findFirst()
                                                        .orElse(scbMerchant.getFeReturnURL());
        if (frontendReturnUrlValue == null) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("orderNumber", scbOrder.getOrderNo());
        params.put("statusCode", statusCode);

        client.callFeReturnURL(frontendReturnUrlValue, params);
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
            throw new AuthException("Invalid key");
        }
    }

    private String generatePrefixTrx() {
        return "SC" + LocalDateTime.now().format(FORMATTER_TRANSACTION_ID_DATETIME) + "B";
    }
}

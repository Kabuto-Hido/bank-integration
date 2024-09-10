package com.example.integratebank.payment;

import com.example.integratebank.dto.CardInfoRequestDTO;
import com.example.integratebank.dto.PaymentDTO;
import com.example.integratebank.dto.SCBConfirmDTO;

import java.util.Map;
import java.util.Optional;

public interface PaymentService {
    Map<String, String> createPayment(PaymentDTO dto);

    SCBConfirmDTO submitPayment(CardInfoRequestDTO dto);

    void getInquiry(String transactionId);

    Optional<Payment> getPaymentByTransactionId(String transactionId);
}

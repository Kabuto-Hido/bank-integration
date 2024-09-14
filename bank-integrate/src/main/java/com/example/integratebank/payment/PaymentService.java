package com.example.integratebank.payment;

import com.example.integratebank.dto.CardInfoRequestDTO;
import com.example.integratebank.dto.DatafeedDTO;
import com.example.integratebank.dto.PaymentDTO;
import com.example.integratebank.dto.SCBConfirmDTO;

import java.util.Map;
import java.util.Optional;

public interface PaymentService {
    Map<String, String> createPayment(PaymentDTO dto);

    SCBConfirmDTO submitPayment(CardInfoRequestDTO dto);

    void getSCBInquiry(String transactionId);

    Optional<Payment> getPaymentByTransactionId(String transactionId);

    void getKBANKInquiry(String transactionId);

    void updateStatus(DatafeedDTO datafeedDTO, String txnNumber);
}

package com.example.integratebank.payment;

import com.example.integratebank.dto.CardInfoRequestDTO;
import com.example.integratebank.dto.DatafeedDTO;
import com.example.integratebank.dto.PaymentDTO;
import com.example.integratebank.dto.SCBConfirmDTO;
import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.enumeration.PaymentStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PaymentService {
    Map<String, String> createPayment(PaymentDTO dto);

    SCBConfirmDTO submitPayment(CardInfoRequestDTO dto);

    void getSCBInquiry(String transactionId);

    Optional<Payment> getPaymentByTransactionId(String transactionId);

    void getKBANKInquiry(String transactionId);

    void updateStatus(DatafeedDTO datafeedDTO, String txnNumber);

    List<Payment> getPaymentByProviderAndStatus(PaymentProvider provider, PaymentStatus status);
}

package com.example.integratebank.payment;

import com.example.integratebank.dto.CardInfoRequestDTO;
import com.example.integratebank.dto.PaymentDTO;

import java.util.Map;

public interface PaymentService {
    Map<String, String> createPayment(PaymentDTO dto);

    void submitPayment(CardInfoRequestDTO dto);
}

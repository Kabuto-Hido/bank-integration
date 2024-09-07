package com.example.bankdemo.scb.payment;

import com.example.bankdemo.scb.dto.PaymentInquiryRequestDTO;
import com.example.bankdemo.scb.dto.PaymentInquiryResponseDTO;
import com.example.bankdemo.scb.dto.PaymentRequestDTO;
import com.example.bankdemo.scb.dto.PaymentResponseDTO;

public interface PaymentService {
    PaymentResponseDTO doPayment(PaymentRequestDTO paymentRequestDTO, String apiKey, String secretKey);

   PaymentInquiryResponseDTO getInquiry(PaymentInquiryRequestDTO paymentInquiryRequestDTO, String apiKey, String secretKey);
}

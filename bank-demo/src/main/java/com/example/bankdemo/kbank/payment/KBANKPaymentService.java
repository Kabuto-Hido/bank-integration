package com.example.bankdemo.kbank.payment;

import com.example.bankdemo.kbank.dto.KBANKInquiryDTO;
import com.example.bankdemo.kbank.dto.KBANKPaymentDTO;

public interface KBANKPaymentService {
    String doPay(KBANKPaymentDTO paymentDTO);

    KBANKInquiryDTO getInquiry(String merchantId, String loginId, String password, String refNo);
}

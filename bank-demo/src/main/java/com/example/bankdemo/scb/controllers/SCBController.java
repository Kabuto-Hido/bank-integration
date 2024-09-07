package com.example.bankdemo.scb.controllers;

import com.example.bankdemo.scb.dto.PaymentInquiryRequestDTO;
import com.example.bankdemo.scb.dto.PaymentInquiryResponseDTO;
import com.example.bankdemo.scb.dto.PaymentRequestDTO;
import com.example.bankdemo.scb.dto.PaymentResponseDTO;
import com.example.bankdemo.scb.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scb")
@RequiredArgsConstructor
public class SCBController {

    private final PaymentService paymentService;

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponseDTO> doPayment(@RequestHeader("merchantSecretKey") String merchantSecretKey,
                                                        @RequestHeader(value = "apikey") String apikey,
                                                        @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.ok(paymentService.doPayment(dto, apikey, merchantSecretKey));
    }

    @PostMapping("/inquiry")
    public ResponseEntity<PaymentInquiryResponseDTO> getPaymentInquiry(@RequestHeader("merchantSecretKey") String merchantSecretKey,
                                                                       @RequestHeader(value = "apikey") String apikey,
                                                                       @RequestBody PaymentInquiryRequestDTO dto) {
        return ResponseEntity.ok(paymentService.getInquiry(dto, apikey, merchantSecretKey));
    }
}

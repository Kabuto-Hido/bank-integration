package com.example.integratebank.controllers;

import com.example.integratebank.dto.SCBDataFeedRequestDTO;
import com.example.integratebank.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class SCBDataFeedController {

    private final PaymentService paymentService;

    /**
     * Use for scb bank call datafeed
     * @param dto SCBDataFeedRequestDTO
     * @return ResponseEntity<String>
     */
    @PostMapping("/datafeed/scb")
    public ResponseEntity<String> handlerDatafeed(SCBDataFeedRequestDTO dto) {
        if (dto == null || dto.getData() == null || dto.getData().getAuthorizeResponse() == null) {
            return ResponseEntity.badRequest().body("Invalid request");
        }

        paymentService.getInquiry(dto.getData().getAuthorizeResponse().getOrderNumber());
        return ResponseEntity.ok("OK");
    }
}

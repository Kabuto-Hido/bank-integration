package com.example.integratebank.controllers;

import com.example.integratebank.dto.SCBDataFeedRequestDTO;
import com.example.integratebank.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SCBDataFeedController {

    private final PaymentService paymentService;

    /**
     * Use for SCB bank call datafeed
     * @param dto SCBDataFeedRequestDTO
     * @return ResponseEntity<String>
     */
    @PostMapping("/datafeed/scb")
    public ResponseEntity<String> handlerDatafeed(SCBDataFeedRequestDTO dto) {
        log.info("Handling scb data feed request: {}", dto);
        if (dto == null || dto.getData() == null || dto.getData().getAuthorizeResponse() == null) {
            return ResponseEntity.badRequest().body("Invalid request");
        }

        paymentService.getSCBInquiry(dto.getData().getAuthorizeResponse().getOrderNumber());
        return ResponseEntity.ok("OK");
    }
}

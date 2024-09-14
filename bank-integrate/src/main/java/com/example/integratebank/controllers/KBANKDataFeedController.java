package com.example.integratebank.controllers;

import com.example.integratebank.dto.SCBDataFeedRequestDTO;
import com.example.integratebank.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KBANKDataFeedController {

    private final PaymentService paymentService;

    /**
     * Use for KBANK bank call datafeed
     * @param refNo String
     * @param status String
     * @return ResponseEntity<String>
     */
    @PostMapping("/datafeed/kbank")
    public ResponseEntity<String> handlerDatafeed(@RequestParam("refNo") String refNo,
                                                  @RequestParam("status") String status) {
        log.info("Handling kbank datafeed request {} {}", refNo, status);
        if (StringUtils.isEmpty(refNo)) {
            return ResponseEntity.badRequest().body("Invalid request");
        }

        paymentService.getSCBInquiry(refNo);
        return ResponseEntity.ok("OK");
    }
}

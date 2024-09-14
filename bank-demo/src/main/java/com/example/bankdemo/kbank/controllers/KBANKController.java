package com.example.bankdemo.kbank.controllers;

import com.example.bankdemo.kbank.dto.KBANKInquiryDTO;
import com.example.bankdemo.kbank.dto.KBANKPaymentDTO;
import com.example.bankdemo.kbank.payment.KBANKPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping("/kbank")
@RequiredArgsConstructor
public class KBANKController {

    private final KBANKPaymentService paymentService;

    @PostMapping("/payment")
    public String doKbankPayment(Model model,
                                    @RequestParam String refNo,
                                    @RequestParam String merchantId,
                                    @RequestParam(name = "total") BigDecimal amount,
                                    @RequestParam(name = "cc") String currency,
                                    @RequestParam String customerEmail,
                                    @RequestParam String channel,
                                    @RequestParam String returnUrl) {
        KBANKPaymentDTO kbankPaymentDTO = new KBANKPaymentDTO();
        kbankPaymentDTO.setRefNo(refNo);
        kbankPaymentDTO.setMerchantId(merchantId);
        kbankPaymentDTO.setAmount(amount);
        kbankPaymentDTO.setCurrency(currency);
        kbankPaymentDTO.setCustomerEmail(customerEmail);
        kbankPaymentDTO.setChannel(channel);
        kbankPaymentDTO.setReturnUrl(returnUrl);
        model.addAttribute("kbankPaymentDTO", kbankPaymentDTO);
        return "kbank-pay";
    }

    @PostMapping("/submit-card-info")
    public String confirmKbankPayment(@ModelAttribute("kbankPaymentDTO") KBANKPaymentDTO paymentDto) {

        String redirectUrl = paymentService.doPay(paymentDto);
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/inquiry")
    public ResponseEntity<KBANKInquiryDTO> getInquiryData(@RequestParam String merchantId,
                                                          @RequestParam String loginId,
                                                          @RequestParam String password,
                                                          @RequestParam String refNo){
        return ResponseEntity.ok(paymentService.getInquiry(merchantId, loginId, password, refNo));
    }

    @PostMapping("/cancel-pay")
    public String cancelPayment(@ModelAttribute("kbankPaymentDTO") KBANKPaymentDTO paymentDto) {
        String redirectUrl = paymentService.cancelPay(paymentDto);
        return "redirect:" + redirectUrl;
    }
}

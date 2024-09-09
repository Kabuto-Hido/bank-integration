package com.example.integratebank.controllers;

import com.example.integratebank.dto.CardInfoRequestDTO;
import com.example.integratebank.dto.PaymentDTO;
import com.example.integratebank.dto.SCBConfirmDTO;
import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PaymentUIController {

    private final PaymentService paymentService;

    @GetMapping("/pay")
    public String pay(Model model) {
        List<String> providers = Arrays.asList(PaymentProvider.SCB.name(), PaymentProvider.KBANK.name());
        PaymentDTO paymentDTO = new PaymentDTO();
        model.addAttribute("paymentDto", paymentDTO);
        model.addAttribute("providers", providers);
        return "pay";
    }

    @PostMapping("/do-pay")
    public String doPay(Model model, @ModelAttribute("paymentDto") PaymentDTO paymentDto) {
        Map<String, String> data = paymentService.createPayment(paymentDto);
        CardInfoRequestDTO cardInfoRequestDTO  = new CardInfoRequestDTO();
        model.addAttribute("cardInfo", cardInfoRequestDTO);
        model.addAttribute("data", data);
        model.addAttribute("bankProvider", paymentDto.getProvider().name());
        if (paymentDto.getProvider().equals(PaymentProvider.KBANK)) {
            model.addAttribute("s", PaymentProvider.KBANK.name());
        }
        return "confirmpay";
    }

    @PostMapping("/confirm-pay")
    public String confirmPay(Model model, @ModelAttribute("cardInfo") CardInfoRequestDTO dto) {
        SCBConfirmDTO scbConfirmDTO = paymentService.submitPayment(dto);
        model.addAttribute("status", scbConfirmDTO.getStatus());
        return "index";
    }
}

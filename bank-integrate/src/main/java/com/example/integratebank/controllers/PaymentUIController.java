package com.example.integratebank.controllers;

import com.example.integratebank.dto.CardInfoRequestDTO;
import com.example.integratebank.dto.PaymentDTO;
import com.example.integratebank.dto.SCBConfirmDTO;
import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.payment.Payment;
import com.example.integratebank.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
        model.addAttribute("bankProvider", paymentDto.getProvider());
        if (paymentDto.getProvider().equals(PaymentProvider.KBANK)) {
            model.addAttribute("s", PaymentProvider.KBANK.name());
        }
        return "confirmpay";
    }

    @PostMapping("/confirm-pay")
    public void confirmPay(@RequestParam PaymentProvider provider,
                           @RequestParam String transactionId,
                           @RequestParam String cardNo,
                           @RequestParam String securityCode,
                           @RequestParam String epMonth,
                           @RequestParam String epYear,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException {

        SCBConfirmDTO scbConfirmDTO = paymentService.submitPayment(CardInfoRequestDTO.builder()
                                                                                     .provider(provider)
                                                                                     .transactionId(transactionId)
                                                                                     .cardNo(cardNo)
                                                                                     .securityCode(securityCode)
                                                                                     .epMonth(epMonth).epYear(epYear)
                                                                                     .build());
        if (Objects.isNull(scbConfirmDTO) || !scbConfirmDTO.isSuccess()) {
            response.sendRedirect(request.getContextPath() + "/fail?order_no=" + transactionId);

        }
        response.sendRedirect(request.getContextPath() + "/paysuccess?ref=" + transactionId);

    }

    /**
     * Handle data bank return to UI
     * @param model Model
     * @param orderNumber String
     * @param statusCode String
     * @return UI
     */
    @GetMapping("/merchant/scb")
    public String redirectSCB(Model model,
                              @RequestParam(value = "orderNumber", required = false) String orderNumber,
                              @RequestParam(value = "statusCode", required = false) String statusCode) {
        // Check transaction id and status code is not null
        if (StringUtils.isEmpty(orderNumber) || StringUtils.isEmpty(statusCode)) {
            return "error";
        }

        // Check is payment exist
        Optional<Payment> paymentOptional = paymentService.getPaymentByTransactionId(orderNumber);
        if (paymentOptional.isEmpty()) {
            return "error";
        }

        if ("1001".equals(statusCode)) {
            return "redirect:/paysuccess?ref=" + orderNumber;
        }

        return "fail";
//        return "redirect:/fail?order_no=" + orderNumber;
    }

    @GetMapping("/paysuccess")
    public String successPage(Model model,
                              @RequestParam(value = "ref", required = false) String ref) {
        // Check transaction id and status code is not null
        if (StringUtils.isEmpty(ref)) {
            return "error";
        }

        // Check is payment exist
        Optional<Payment> paymentOptional = paymentService.getPaymentByTransactionId(ref);
        if (paymentOptional.isEmpty()) {
            return "error";
        }

        Payment payment = paymentOptional.get();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        model.addAttribute("datetime", dateFormat.format(Timestamp.valueOf(payment.getUpdateDate())));
        model.addAttribute("transactionId", payment.getTransactionId());
        model.addAttribute("provider", payment.getProvider().name());

        BigDecimal amount;
        if (payment.getPaidAmount() != null) {
            amount = payment.getPaidAmount();
        } else {
            // emergency case - this possible if customer try to open paycardsuccess for payment without datafeed, e.g. expired
            amount = payment.getAmount();
        }
        model.addAttribute("amount", amount);
        return "index";
    }

    @GetMapping("/fail")
    public String failPage(Model model,
                              @RequestParam(value = "ref", required = false) String ref) {
        return "fail";
    }
}

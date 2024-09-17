package com.example.integratebank.controllers;

import com.example.integratebank.bank.client.KBANKClient;
import com.example.integratebank.dto.CardInfoRequestDTO;
import com.example.integratebank.dto.DatafeedDTO;
import com.example.integratebank.dto.KBANKInquiryResponseDTO;
import com.example.integratebank.dto.PaymentDTO;
import com.example.integratebank.dto.SCBConfirmDTO;
import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.enumeration.PaymentStatus;
import com.example.integratebank.payment.Payment;
import com.example.integratebank.payment.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PaymentUIController {

    private final PaymentService paymentService;

    private final KBANKClient kbankClient;

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
            model.addAttribute("agreementMsg", "Payment Terms and Conditions");
            return "confirm_pay";
        }
        return "card_pay";
    }

    /**
     * Case use card to payment with bank (SCB)
     * @param provider PaymentProvider
     * @param transactionId String
     * @param cardNo String
     * @param securityCode String
     * @param epMonth String
     * @param epYear String
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws IOException
     */
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
     * Handle data from SCB bank return to UI
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

        return "redirect:/fail?ref=" + orderNumber;
    }

    /**
     * Handle data from KBANK bank return to UI
     * @param model Model
     * @param transactionId String
     * @return UI
     */
    @GetMapping("/merchant/kbank")
    public String redirectKBANK(Model model,
                              @RequestParam(value = "refNo", required = false) String transactionId) {
        // Check transaction id and status code is not null
        if (StringUtils.isEmpty(transactionId)) {
            return "error";
        }

        // Check is payment exist
        Optional<Payment> paymentOptional = paymentService.getPaymentByTransactionId(transactionId);
        if (paymentOptional.isEmpty()) {
            return "error";
        }

        // get inquiry
        Optional<KBANKInquiryResponseDTO> optionalKBANKInquiryResponseDTO = kbankClient.getPaymentStatus(transactionId);
        if (optionalKBANKInquiryResponseDTO.isEmpty()) {
            return "redirect:/fail?ref=" + transactionId;
        }

        if (KBANKInquiryResponseDTO.KBANKStatus.CANCEL.equals(optionalKBANKInquiryResponseDTO.get().getStatus())) {
            return "redirect:/cancel?ref=" + transactionId;
        }

        // case success
        if (KBANKInquiryResponseDTO.KBANKStatus.SUCCESS.equals(optionalKBANKInquiryResponseDTO.get().getStatus())) {
            return "redirect:/paysuccess?ref=" + transactionId;
        }

        return "redirect:/fail?ref=" + transactionId;
    }

    /**
     * handle success page
     * @param model Model
     * @param ref String
     * @return UI page success
     */
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
        return "success";
    }

    /**
     * Handle page fail
     * @param model Model
     * @param ref String
     * @return UI Page fail
     */
    @GetMapping("/fail")
    public String failPage(Model model,
                              @RequestParam(value = "ref", required = false) String ref) {
        // Check transaction id and status code is not null
        if (StringUtils.isEmpty(ref)) {
            Date currDate = Date.from(Instant.now());
            model.addAttribute("time", formatTime(currDate));
            model.addAttribute("date", formatDate(currDate));
            return "fail";
        }

        // Check is payment exist
        Optional<Payment> paymentOptional = paymentService.getPaymentByTransactionId(ref);
        if (paymentOptional.isEmpty()) {
            return "error";
        }

        Payment payment = paymentOptional.get();
        model.addAttribute("time", formatTime(Timestamp.valueOf(payment.getCreateDate())));
        model.addAttribute("date", formatDate(Timestamp.valueOf(payment.getCreateDate())));
        model.addAttribute("transactionId", payment.getTransactionId());
        return "fail";
    }

    /**
     * Handle page cancel
     * @param model Model
     * @param ref String
     * @return UI Page cancel
     */
    @GetMapping("/cancel")
    public String cancelPage(Model model,
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
        DatafeedDTO datafeedDTO = DatafeedDTO.builder().transactionId(ref)
                                             .paymentStatus(PaymentStatus.CANCEL).build();
        paymentService.updateStatus(datafeedDTO, null);
        return "cancel";
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
        return formatter.format(date);
    }

    private String formatTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }
}

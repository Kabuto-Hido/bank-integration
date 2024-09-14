package com.example.bankdemo.kbank.payment;

import com.example.bankdemo.exception.AuthException;
import com.example.bankdemo.exception.NotFoundException;
import com.example.bankdemo.kbank.client.CallBackClient;
import com.example.bankdemo.kbank.dto.KBANKInquiryDTO;
import com.example.bankdemo.kbank.dto.KBANKPaymentDTO;
import com.example.bankdemo.kbank.enumration.KBANKStatus;
import com.example.bankdemo.kbank.merchant.KBANKMerchant;
import com.example.bankdemo.kbank.merchant.KBANKMerchantService;
import com.example.bankdemo.kbank.order.KBANKOrder;
import com.example.bankdemo.kbank.order.KBANKOrderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KBANKPaymentServiceImpl implements KBANKPaymentService {

    private final KBANKMerchantService kbankMerchantService;

    private final KBANKOrderService kbankOrderService;

    private final CallBackClient callBackClient;

    public final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);

    private final DateTimeFormatter FORMATTER_TRANSACTION_ID_DATETIME =
            DateTimeFormatter.ofPattern("ddMMyyHHmmss");

    @Override
    public String doPay(KBANKPaymentDTO paymentDTO) {
        Optional<KBANKMerchant> kbankMerchantOptional = kbankMerchantService.getByMerchantId(paymentDTO.getMerchantId());
        if (kbankMerchantOptional.isEmpty()) {
            throw new AuthException("Invalid merchant id");
        }

        KBANKMerchant kbankMerchant = kbankMerchantOptional.get();
        BigDecimal fee = kbankMerchant.getFee();
        BigDecimal feeValue = paymentDTO.getAmount().multiply(fee).divide(ONE_HUNDRED, MC);
        BigDecimal totalAmount = paymentDTO.getAmount().add(feeValue, MC);

        KBANKStatus status = KBANKStatus.PENDING;
        if (paymentDTO.getCardNumber().startsWith("434")) {
            status = KBANKStatus.SUCCESS;
        } else if (paymentDTO.getCardNumber().startsWith("435")) {
            status = KBANKStatus.FAIL;
        }

        KBANKOrder kbankOrder = kbankOrderService.save(new KBANKOrder(
                paymentDTO.getRefNo(),
                generatePrefixTrx(),
                paymentDTO.getAmount(),
                totalAmount,
                paymentDTO.getCurrency(),
                paymentDTO.getCustomerEmail(),
                status,
                kbankMerchant
        ));

        // callback datafeed
        callbackDataFeed(kbankMerchant, kbankOrder, status);

        String returnUrl = StringUtils.defaultIfEmpty(paymentDTO.getReturnUrl(),
                                                      kbankMerchant.getFeReturnURL());

        if (StringUtils.isEmpty(returnUrl)) {
            throw new NotFoundException("Return url is empty");
        }

        return returnUrl + "?refNo=" + paymentDTO.getRefNo();
    }

    @Override
    public KBANKInquiryDTO getInquiry(String merchantId, String loginId, String password, String refNo) {
        KBANKMerchant kbankMerchant = kbankMerchantService.getByMerchantId(merchantId)
                                                          .orElseThrow(() -> new NotFoundException("Not found with merchant id"));

        authorizationPayment(loginId, password, kbankMerchant);

        KBANKOrder kbankOrder = kbankOrderService.findByRefNoAndMerchantId(refNo, merchantId)
                .orElseThrow(() -> new NotFoundException("Not found transaction"));

        return KBANKInquiryDTO.builder()
                              .ref(kbankOrder.getRefNo())
                              .baseAmount(kbankOrder.getAmount())
                              .currency(kbankOrder.getCurrency())
                              .payRef(kbankOrder.getTransactionId())
                              .status(kbankOrder.getStatus())
                              .totalAmount(kbankOrder.getTotalAmount()).build();
    }

    private void authorizationPayment(String loginId, String password, KBANKMerchant kbankMerchant) {
        if (!kbankMerchant.getLoginId().equals(loginId) ||
                !kbankMerchant.getPassword().equals(password)) {
            throw new AuthException("Unauthorized");
        }
    }

    private void callbackDataFeed(KBANKMerchant kbankMerchant, KBANKOrder kbankOrder, KBANKStatus status) {
        String datafeedUrl = kbankMerchant.getBeReturnURL();
        if (StringUtils.isEmpty(datafeedUrl)) {
            return;
        }

        Map<String, String> params  = new HashMap<>();
        params.put("refNo", kbankOrder.getRefNo());
        params.put("status", status.name());

        callBackClient.callbackDatafeed(datafeedUrl, params);
    }

    private String generatePrefixTrx() {
        return "K" + LocalDateTime.now().format(FORMATTER_TRANSACTION_ID_DATETIME) + "BANK";
    }
}

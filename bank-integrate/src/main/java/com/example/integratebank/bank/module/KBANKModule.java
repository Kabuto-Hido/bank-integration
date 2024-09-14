package com.example.integratebank.bank.module;

import com.example.integratebank.dto.PaymentDTO;
import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.payment.Payment;
import com.example.integratebank.util.PaymentUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KBANKModule extends DefaultModule {

    @Value("${kbank.payment.url}")
    private String actionUrl;

    @Value("${kbank.return.url}")
    private String returnUrl;

    @Value("${kbank.merchant.id}")
    private String merchantId;

    public KBANKModule() {
        super(PaymentProvider.KBANK);
    }

    @Override
    public Map<String, String> getOrderProps(Payment payment, PaymentDTO paymentDTO) {
        Map<String, String> props = new HashMap<>();
        props.put("refNo", payment.getTransactionId());
        props.put("merchantId", merchantId);
        props.put("customerEmail", StringUtils.isEmpty(paymentDTO.getEmail()) ? null : paymentDTO.getEmail());
        // currency THB or USD
//        props.put("cc", "THB");
        props.put("total", PaymentUtil.formatDecimal(payment.getAmount()));
        // full -> Full Payment VISA Mastercard
        props.put("channel", "full");
        props.put("returnUrl", returnUrl + "?refNo=" + payment.getTransactionId());
        props.put("actionUrl", actionUrl);
        return props;
    }
}

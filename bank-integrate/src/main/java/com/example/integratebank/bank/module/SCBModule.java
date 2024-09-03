package com.example.integratebank.bank.module;

import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.payment.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SCBModule extends DefaultModule {

    @Value("${scb.payment.url}")
    private String actionUrl;

    public SCBModule() {
        super(PaymentProvider.SCB);
    }

    @Override
    public Map<String, String> getOrderProps(Payment payment) {
        Map<String, String> props = new HashMap<>();
        props.put("orderNumber", payment.getTransactionId());
        props.put("actionUrl", actionUrl);
        return props;
    }
}

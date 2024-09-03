package com.example.integratebank.bank.module;

import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.payment.Payment;

import java.util.Map;

public interface PaymentModule {
    PaymentProvider getPaymentProvider();

    Map<String, String> getOrderProps(Payment payment);
}

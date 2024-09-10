package com.example.integratebank.bank.module;

import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.payment.Payment;

import java.util.Map;

public interface PaymentModule {
    /**
     * Define using which provider module
     * @return PaymentProvider
     */
    PaymentProvider getPaymentProvider();

    /**
     * Get param need to call payment with bank
     * @param payment Payment
     * @return Map<String, String>
     */
    Map<String, String> getOrderProps(Payment payment);
}

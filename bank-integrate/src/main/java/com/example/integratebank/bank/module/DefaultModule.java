package com.example.integratebank.bank.module;

import com.example.integratebank.dto.PaymentDTO;
import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.payment.Payment;

import java.util.Map;
import java.util.Optional;

public abstract class DefaultModule implements PaymentModule {

    protected final PaymentProvider provider;

    protected DefaultModule(PaymentProvider provider) {
        this.provider = provider;
    }

    @Override
    public PaymentProvider getPaymentProvider() {
        return provider;
    }

    @Override
    public abstract Map<String, String> getOrderProps(Payment payment, PaymentDTO paymentDTO);
}

package com.example.integratebank.bank;

import com.example.integratebank.bank.module.PaymentModule;
import com.example.integratebank.enumeration.PaymentProvider;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class BankConfig {

    @Bean
    public Map<PaymentProvider, PaymentModule> getBankAPI(
            @Nonnull Collection<PaymentModule> providers) {
        return providers.stream()
                        .collect(
                                Collectors.toMap(PaymentModule::getPaymentProvider, Function.identity()));
    }
}

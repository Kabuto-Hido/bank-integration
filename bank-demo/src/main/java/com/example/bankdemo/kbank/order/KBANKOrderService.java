package com.example.bankdemo.kbank.order;

import java.util.Optional;

public interface KBANKOrderService {
    KBANKOrder save(KBANKOrder order);

    Optional<KBANKOrder> findByTransactionIdAndMerchantId(String transactionId, String merchantId);

    Optional<KBANKOrder> findByRefNoAndMerchantId(String refNo, String merchantId);
}

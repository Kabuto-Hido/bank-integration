package com.example.bankdemo.scb.order;

import java.util.Optional;

public interface SCBOrderService {
    SCBOrder save(SCBOrder order);

    Optional<SCBOrder> findByTransactionIdAndMerchantId(String transactionId, String merchantId);

    Optional<SCBOrder> findByOrderNoAndMerchantId(String orderNo, String merchantId);
}

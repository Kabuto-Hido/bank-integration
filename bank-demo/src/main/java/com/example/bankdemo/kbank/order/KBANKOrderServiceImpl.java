package com.example.bankdemo.kbank.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KBANKOrderServiceImpl implements KBANKOrderService {

    private final KBANKOrderRepository repository;

    @Transactional
    @Override
    public KBANKOrder save(KBANKOrder order) {
        return repository.saveAndFlush(order);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<KBANKOrder> findByTransactionIdAndMerchantId(String transactionId, String merchantId) {
        return repository.findByTransactionIdAndKbankMerchantMerchantId(transactionId, merchantId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<KBANKOrder> findByRefNoAndMerchantId(String refNo, String merchantId) {
        return repository.findByRefNoAndKbankMerchantMerchantId(refNo, merchantId);
    }
}

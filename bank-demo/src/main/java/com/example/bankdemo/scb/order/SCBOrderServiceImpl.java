package com.example.bankdemo.scb.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SCBOrderServiceImpl implements SCBOrderService {

    private final SCBOrderRepository repository;

    @Transactional
    @Override
    public SCBOrder save(SCBOrder order) {
        return repository.saveAndFlush(order);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<SCBOrder> findByTransactionIdAndMerchantId(String transactionId, String merchantId) {
        return repository.findByTransactionIdAndScbMerchantMerchantId(transactionId, merchantId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<SCBOrder> findByOrderNoAndMerchantId(String orderNo, String merchantId) {
        return repository.findByOrderNoAndScbMerchantMerchantId(orderNo, merchantId);
    }

}

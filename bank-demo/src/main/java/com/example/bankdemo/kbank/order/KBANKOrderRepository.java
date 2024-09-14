package com.example.bankdemo.kbank.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KBANKOrderRepository extends JpaRepository<KBANKOrder, Long> {
    Optional<KBANKOrder> findByRefNoAndKbankMerchantMerchantId(String refNo, String merchantId);

    Optional<KBANKOrder> findByTransactionIdAndKbankMerchantMerchantId(String transactionId, String merchantId);
}

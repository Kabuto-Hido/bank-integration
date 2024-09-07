package com.example.bankdemo.scb.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SCBOrderRepository extends JpaRepository<SCBOrder, Long> {
    Optional<SCBOrder> findByTransactionIdAndScbMerchantMerchantId(String transactionId, String scbMerchantId);

    Optional<SCBOrder> findByOrderNoAndScbMerchantMerchantId(String orderNo, String scbMerchantId);
}

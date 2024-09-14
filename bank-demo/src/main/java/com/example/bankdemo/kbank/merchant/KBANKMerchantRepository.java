package com.example.bankdemo.kbank.merchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KBANKMerchantRepository extends JpaRepository<KBANKMerchant, Long> {
    Optional<KBANKMerchant> findByMerchantId(String merchantId);
}

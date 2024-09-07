package com.example.bankdemo.scb.merchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SCBMerchantRepository extends JpaRepository<SCBMerchant, Long> {

    Optional<SCBMerchant> findByMerchantId(String merchantId);
}

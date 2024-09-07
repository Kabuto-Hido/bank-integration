package com.example.bankdemo.scb.merchant;

import java.util.Optional;

public interface SCBMerchantService {
    Optional<SCBMerchant> findByMerchantId(String merchantId);
}

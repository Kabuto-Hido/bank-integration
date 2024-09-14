package com.example.bankdemo.kbank.merchant;

import java.util.Optional;

public interface KBANKMerchantService {
    Optional<KBANKMerchant> getByMerchantId(String merchantId);
}

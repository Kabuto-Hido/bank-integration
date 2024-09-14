package com.example.bankdemo.kbank.merchant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KBANKMerchantServiceImpl implements KBANKMerchantService {

    private final KBANKMerchantRepository repository;

    @Transactional(readOnly = true)
    @Override
    public Optional<KBANKMerchant> getByMerchantId(String merchantId) {
        return repository.findByMerchantId(merchantId);
    }
}

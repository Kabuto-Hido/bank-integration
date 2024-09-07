package com.example.bankdemo.scb.merchant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SCBMerchantServiceImpl implements SCBMerchantService {

    private final SCBMerchantRepository merchantRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<SCBMerchant> findByMerchantId(String merchantId) {
        return merchantRepository.findByMerchantId(merchantId);
    }
}

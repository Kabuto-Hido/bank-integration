package com.example.bankdemo.kbank.merchant;

import com.example.bankdemo.kbank.order.KBANKOrder;
import com.example.bankdemo.scb.order.SCBOrder;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "kbank_merchant")
@Entity
@DynamicInsert
@DynamicUpdate
@Data
public class KBANKMerchant {
    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", unique = true, nullable = false)
    private String merchantId;

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "fee")
    private BigDecimal fee;

    @Column(name = "fe_return_url")
    private String feReturnURL;

    @Column(name = "be_return_url")
    private String beReturnURL;

    @OneToMany(mappedBy = "kbankMerchant")
    private final List<KBANKOrder> orders = Collections.emptyList();

    @Getter
    @CreationTimestamp
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    public KBANKMerchant(String merchantId, String loginId, String password, BigDecimal fee) {
        this.merchantId = merchantId;
        this.loginId = loginId;
        this.password = password;
        this.fee = fee;
    }
}

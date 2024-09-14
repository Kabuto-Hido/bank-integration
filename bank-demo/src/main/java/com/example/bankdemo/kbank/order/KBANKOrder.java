package com.example.bankdemo.kbank.order;

import com.example.bankdemo.kbank.enumration.KBANKStatus;
import com.example.bankdemo.kbank.merchant.KBANKMerchant;
import com.example.bankdemo.scb.merchant.SCBMerchant;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "kbank_order")
@Entity
@DynamicInsert
@DynamicUpdate
@Data
public class KBANKOrder {

    @Getter
    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_no", unique = true)
    private String refNo;

    @Transient
    private String txPrefix;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private KBANKStatus status = KBANKStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private KBANKMerchant kbankMerchant;

    @Getter
    @CreationTimestamp
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Getter
    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    public KBANKOrder(String refNo, String txPrefix, BigDecimal amount, BigDecimal totalAmount,
                      String currency, String customerEmail, KBANKStatus status, KBANKMerchant kbankMerchant) {
        this.refNo = refNo;
        this.txPrefix = txPrefix;
        this.amount = amount;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.customerEmail = customerEmail;
        this.status = status;
        this.kbankMerchant = kbankMerchant;
    }

    public void setId(Long id) {
        this.id = id;
        // Generate transactionId if it's a new entity
        if (transactionId == null) {
            transactionId = txPrefix + id;
        }
    }
}

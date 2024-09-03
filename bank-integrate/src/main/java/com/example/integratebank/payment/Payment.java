package com.example.integratebank.payment;

import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.enumeration.PaymentStatus;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment")
@Entity
@DynamicInsert
@DynamicUpdate
public class Payment {
    @Id
    @Getter
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String txPrefix;

    @Getter
    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Getter
    @Column(name = "amount")
    private BigDecimal amount;

    @Getter
    @Column(name = "fee")
    private BigDecimal fee;

    @Getter
    @Column(name = "paid_amount")
    private BigDecimal paidAmount;

    @Getter
    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private PaymentProvider provider;

    @Getter
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @CreationTimestamp
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    public Payment(String txPrefix, BigDecimal amount, BigDecimal fee, BigDecimal paidAmount,
                   PaymentProvider provider, PaymentStatus status) {
        this.txPrefix = txPrefix;
        this.amount = amount;
        this.provider = provider;
        this.status = status;
        this.fee = fee;
        this.paidAmount = paidAmount;
    }

    public void setId(Long id) {
        this.id = id;
        // Generate transactionId if it's a new entity
        if (transactionId == null) {
            transactionId = txPrefix + id;
        }
    }
}

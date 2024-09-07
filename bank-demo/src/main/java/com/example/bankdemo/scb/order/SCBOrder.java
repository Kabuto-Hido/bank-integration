package com.example.bankdemo.scb.order;

import com.example.bankdemo.scb.enumeration.SCBStatus;
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
@Table(name = "scb_order")
@Entity
@DynamicInsert
@DynamicUpdate
@Data
public class SCBOrder {

    @Getter
    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true)
    private String orderNo;

    @Transient
    private String txPrefix;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SCBStatus status = SCBStatus.PENDING;

    @Column(name = "status_code")
    private String statusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private SCBMerchant scbMerchant;

    @Getter
    @CreationTimestamp
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    @Getter
    @UpdateTimestamp
    @Column(name = "update_date")
    private LocalDateTime updateDate;

    public SCBOrder(String orderNo, String txPrefix, BigDecimal amount, String customerId,
                    SCBStatus status, String statusCode, SCBMerchant scbMerchant) {
        this.orderNo = orderNo;
        this.txPrefix = txPrefix;
        this.amount = amount;
        this.customerId = customerId;
        this.status = status;
        this.statusCode = statusCode;
        this.scbMerchant = scbMerchant;
    }

    public void setId(Long id) {
        this.id = id;
        // Generate transactionId if it's a new entity
        if (transactionId == null) {
            transactionId = txPrefix + id;
        }
    }
}

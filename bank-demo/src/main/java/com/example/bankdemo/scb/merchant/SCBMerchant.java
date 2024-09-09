package com.example.bankdemo.scb.merchant;

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
@Table(name = "scb_merchant")
@Entity
@DynamicInsert
@DynamicUpdate
@Data
public class SCBMerchant {
    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", unique = true, nullable = false)
    private String merchantId;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "secret_key", nullable = false)
    private String secretKey;

    @Column(name = "fee")
    private BigDecimal fee;

    @Column(name = "fe_return_url")
    private String feReturnURL;

    @Column(name = "be_return_url")
    private String beReturnURL;

    @OneToMany(mappedBy = "scbMerchant")
    private final List<SCBOrder> orders = Collections.emptyList();

    @Getter
    @CreationTimestamp
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;

    public SCBMerchant(String merchantId, String apiKey, String secretKey, BigDecimal fee) {
        this.merchantId = merchantId;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.fee = fee;
    }
}

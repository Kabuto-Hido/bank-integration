package com.example.integratebank.datafeed;

import com.example.integratebank.dto.DatafeedDTO;
import com.example.integratebank.enumeration.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Entity
@Table(name = "datafeed")
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
public class Datafeed {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "amount")
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "date_of_creation")
    private Date dateOfCreation;

    @Convert(converter = DataFeedResponseConverter.class)
    private Map<String, Object> response;

    public Datafeed(DatafeedDTO dto) {
        this.transactionId = dto.getTransactionId();
        this.amount = dto.getAmount();
        this.paymentStatus = dto.getPaymentStatus();
        this.dateOfCreation = dto.getDateOfCreation();
        this.response = dto.getResponse();
    }
}

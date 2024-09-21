package com.example.integratebank.job;

import com.example.integratebank.bank.client.SCBClient;
import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.enumeration.PaymentStatus;
import com.example.integratebank.payment.Payment;
import com.example.integratebank.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Job run every hour to
 * get all SCB payment status PENDING
 * check status with bank
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SCBJob {

    private final PaymentService paymentService;

    @Value("${scb.job.enable:false}")
    private boolean jobEnable;

    @Scheduled(cron = "${scb.job.execute:0 0 * * * *}")
    public void jobCheck() {
        if (Boolean.FALSE.equals(jobEnable)) {
            log.info("Job SCB Bank [Disable]");
            return;
        }

        List<Payment> paymentList = paymentService.getPaymentByProviderAndStatus(PaymentProvider.SCB,
                                                                                 PaymentStatus.PENDING);
        if (CollectionUtils.isEmpty(paymentList)) {
            log.info("No SCB payment is pending [Skip]");
            return;
        }

        paymentList.forEach(payment -> paymentService.getSCBInquiry(payment.getTransactionId()));
    }
}

package com.example.integratebank.job;

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
 * Job run every 30m to
 * get all KBANK payment status PENDING
 * check status with bank
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KBankJob {

    private final PaymentService paymentService;

    @Value("${kbank.job.enable:false}")
    private boolean jobEnable;

    @Scheduled(cron = "${kbank.job.execute:0 */30 * * * *}")
    public void jobCheck() {
        if (Boolean.FALSE.equals(jobEnable)) {
            log.info("Job KBANK Bank [Disable]");
            return;
        }

        List<Payment> paymentList = paymentService.getPaymentByProviderAndStatus(PaymentProvider.KBANK,
                                                                                 PaymentStatus.PENDING);
        if (CollectionUtils.isEmpty(paymentList)) {
            log.info("No KBANK payment is pending [Skip]");
            return;
        }

        paymentList.forEach(payment -> paymentService.getKBANKInquiry(payment.getTransactionId()));
    }
}

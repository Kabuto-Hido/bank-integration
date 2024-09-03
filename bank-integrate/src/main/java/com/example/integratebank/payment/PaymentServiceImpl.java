package com.example.integratebank.payment;

import com.example.integratebank.bank.module.PaymentModule;
import com.example.integratebank.customer.Customer;
import com.example.integratebank.customer.CustomerRepository;
import com.example.integratebank.dto.CardInfoRequestDTO;
import com.example.integratebank.dto.PaymentDTO;
import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.enumeration.PaymentStatus;
import com.example.integratebank.exception.BadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final CustomerRepository customerRepository;

    private final Map<PaymentProvider, PaymentModule> paymentModules;

    @Value("${tx.prefix}")
    private String txPrefix;

    //Create payment
    @Transactional
    @Override
    public Map<String, String> createPayment(PaymentDTO dto) {
        Payment payment = paymentRepository.saveAndFlush(new Payment(generateTrx(dto.getProvider().name()),
                                                                     dto.getAmount(), null, dto.getAmount(),
                                                                     dto.getProvider(), PaymentStatus.PENDING));

        // save customer info
        customerRepository.save(new Customer(payment, dto.getMessage(), dto.getEmail(), dto.getLastname(),
                                             dto.getFirstname()));


        return paymentModules.get(dto.getProvider()).getOrderProps(payment);
    }

    @Override
    public void submitPayment(CardInfoRequestDTO dto) {
        Payment payment = paymentRepository.findByTransactionId(dto.getTransactionId())
                .orElseThrow(() -> new BadRequest("Payment not found"));
        
    }

    public String generateTrx(String provider) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        String dateStr = dateFormat.format(new Date());
        return txPrefix + provider + dateStr;
    }
}

package com.example.integratebank.payment;

import com.example.integratebank.bank.client.SCBClient;
import com.example.integratebank.bank.module.PaymentModule;
import com.example.integratebank.constants.MessageConstants;
import com.example.integratebank.customer.Customer;
import com.example.integratebank.customer.CustomerRepository;
import com.example.integratebank.datafeed.DatafeedService;
import com.example.integratebank.dto.CardInfoRequestDTO;
import com.example.integratebank.dto.DatafeedDTO;
import com.example.integratebank.dto.PaymentDTO;
import com.example.integratebank.dto.SCBConfirmDTO;
import com.example.integratebank.dto.SCBInquiryRequestDTO;
import com.example.integratebank.dto.SCBInquiryResponseDTO;
import com.example.integratebank.dto.ScbBankRequestDTO;
import com.example.integratebank.dto.ScbPurchaseResponseDTO;
import com.example.integratebank.enumeration.CardType;
import com.example.integratebank.enumeration.PaymentProvider;
import com.example.integratebank.enumeration.PaymentStatus;
import com.example.integratebank.exception.BadRequest;
import com.example.integratebank.exception.DuplicateException;
import com.example.integratebank.util.PaymentUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final CustomerRepository customerRepository;

    private final DatafeedService datafeedService;

    private final Map<PaymentProvider, PaymentModule> paymentModules;

    private final PaymentUtil paymentUtil;

    private final SCBClient scbClient;

    @Value("${tx.prefix}")
    private String txPrefix;

    @Value("${scb.merchant.id}")
    private String scbMerchantId;

    @Value("${scb.fe.return.url}")
    private String scbFeReturnUrl;

    @Value("${scb.be.return.url}")
    private String scbBeReturnUrl;

    @Value("${scb.payment.url}")
    private String scbActionUrl;

    /**
     * create payment transaction
     * @param dto PaymentDTO
     * @return Map<String, String>
     */
    //Create payment
    @Transactional
    @Override
    public Map<String, String> createPayment(PaymentDTO dto) {
        try {
            Payment payment = paymentRepository.saveAndFlush(new Payment(generateTrx(dto.getProvider().name()),
                                                                         dto.getAmount(), null, dto.getAmount(),
                                                                         dto.getProvider(), PaymentStatus.PENDING));

            // save customer info
            customerRepository.save(new Customer(payment, dto.getMessage(), dto.getEmail(), dto.getLastname(),
                                                 dto.getFirstname()));

            return paymentModules.get(dto.getProvider()).getOrderProps(payment);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateException(ex.getMessage());
        }

    }

    /**
     * In case insert card in your page
     * submit payment with card information
     * @param dto CardInfoRequestDTO
     * @return SCBConfirmDTO
     */
    @Override
    public SCBConfirmDTO submitPayment(CardInfoRequestDTO dto) {
        if (!dto.getProvider().equals(PaymentProvider.SCB)) {
            throw new BadRequest("Only SCB providers are supported");
        }

        Payment payment = paymentRepository.findByTransactionId(dto.getTransactionId())
                .orElseThrow(() -> new BadRequest("Payment not found"));

        CardType cardType = CardType.fromCardNumber(dto.getCardNo());

        // encrypt card
        HashMap<String, String> cardInfo = new HashMap<>();
        cardInfo.put("cardNumber", dto.getCardNo());
        cardInfo.put("cardType", cardType.name());
        cardInfo.put("cvv", dto.getSecurityCode());
        cardInfo.put("expiryMonth", dto.getEpMonth());
        cardInfo.put("expiryYear", dto.getEpYear());
        String cardEncrypt = paymentUtil.encryptObject(cardInfo, MessageConstants.SUBMIT_PAYMENT_FAIL);

        ScbBankRequestDTO.PaymentInfo paymentInfo = new ScbBankRequestDTO.PaymentInfo(
                dto.getTransactionId(),
                paymentUtil.getCurrentDateFormatted(payment.getCreateDate()),
                "THB", payment.getAmount(), paymentUtil.generateCustomerId(),
                "A"
        );

        /* Custom return url to direct to your page
        * Fe URL -> UI
        * Be URL -> call datafeed -> check status transaction
        * */
        List<ScbBankRequestDTO.OtherInfo> otherInfos = new ArrayList<>();
        otherInfos.add(new ScbBankRequestDTO.OtherInfo("frontendReturnUrl", scbFeReturnUrl));
        otherInfos.add(new ScbBankRequestDTO.OtherInfo("backendReturnUrl", scbBeReturnUrl));

        ScbBankRequestDTO scbBankRequestDTO = new ScbBankRequestDTO(
                scbMerchantId, paymentInfo, otherInfos, cardEncrypt
        );

        // call confirm payment to bank
        Optional<ScbPurchaseResponseDTO> responseDTOOptional = scbClient.confirmPayment(scbActionUrl,
                                                                                        scbBankRequestDTO);
        if (responseDTOOptional.isEmpty()) {
            log.error("Payment confirmation failed");
            // update fail status
            updateStatus(DatafeedDTO.builder().transactionId(dto.getTransactionId())
                                    .paymentStatus(PaymentStatus.FAIL).build(), null);
            return new SCBConfirmDTO(false, PaymentStatus.FAIL.name());
        }

        return processBankResponse(dto.getTransactionId(),responseDTOOptional.get());
    }

    /**
     * Check response from bank
     * @param transactionId String
     * @param bankResponse ScbPurchaseResponseDTO
     * return SCBConfirmDTO
     */
    private SCBConfirmDTO processBankResponse(String transactionId, ScbPurchaseResponseDTO bankResponse) {
        ScbPurchaseResponseDTO.AuthorizeResponse authorizeResponse = bankResponse.getData().getAuthorizeResponse();
        if (Objects.nonNull(authorizeResponse)) {
            if (authorizeResponse.getStatusCode().equals("1001")) {
                // get inquiry by call api from bank
                getInquiry(transactionId);
                return new SCBConfirmDTO(true, PaymentStatus.COMPLETE.name());
            }
        }

        return new SCBConfirmDTO(false, PaymentStatus.PENDING.name());
    }

    @Override
    public void getInquiry(String transactionId) {
        // prepare inquiry request dto
        SCBInquiryRequestDTO scbInquiryRequestDTO = new SCBInquiryRequestDTO(scbMerchantId, transactionId);

        SCBInquiryResponseDTO scbInquiryResponseDTO = scbClient.getTransactionInquiry("http://localhost:9002/scb/inquiry",
                                                                          scbInquiryRequestDTO)
                                                               .orElseThrow(() -> new BadRequest("Inquiry not found"));

        processDataFeed(transactionId, scbInquiryResponseDTO);
    }

    /**
     * handle datafeed and update to payment
     * @param transactionId String
     * @param scbInquiryResponseDTO SCBInquiryResponseDTO
     */
    private void processDataFeed(String transactionId, SCBInquiryResponseDTO scbInquiryResponseDTO) {
        if (Objects.isNull(scbInquiryResponseDTO.getData())) {
            return;
        }

        SCBInquiryResponseDTO.InquiryData data = scbInquiryResponseDTO.getData();
        //migrate status code of bank to payment status
        PaymentStatus bankStatus = getPaymentStatus(data.getStatusCode());
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseMap = objectMapper.convertValue(scbInquiryResponseDTO, new TypeReference<>() {
        });

        //init datafeed
        DatafeedDTO datafeedDTO = DatafeedDTO.builder()
                                             .paymentStatus(bankStatus)
                                             .transactionId(transactionId)
                                             .amount(data.getAmount())
                                             .response(responseMap)
                                             .build();

        // add datafeed in db
        datafeedService.addDataFeed(datafeedDTO);

        // update status into payment
        updateStatus(datafeedDTO, data.getTxnNumber());
    }

    /**
     * update status when get inquiry from bank
     * @param datafeedDTO DatafeedDTO
     * @param txnNumber String
     */
    private void updateStatus(DatafeedDTO datafeedDTO, String txnNumber) {
        paymentRepository.findByTransactionId(datafeedDTO.getTransactionId())
                         .ifPresent(payment -> {
                             if (payment.getStatus() != PaymentStatus.PENDING) {
                                 return;
                             }

                             // case success -> calculate fee and paid amount
                             if (datafeedDTO.getPaymentStatus().equals(PaymentStatus.COMPLETE)) {
                                final BigDecimal fee = datafeedDTO.getAmount().subtract(payment.getAmount());
                                 payment.setFee(fee);
                                 payment.setPaidAmount(fee.add(payment.getAmount()));
                             }

                             payment.setStatus(datafeedDTO.getPaymentStatus());
                             payment.setProviderRef(txnNumber);
                             paymentRepository.save(payment);
                         });
    }

    /**
     * mapping status code of bank to payment status
     * @param statusCode String
     * @return PaymentStatus
     */
    private PaymentStatus getPaymentStatus(String statusCode) {
        return switch (statusCode) {
            case "1002", "4002" -> PaymentStatus.FAIL;
            case "2001" -> PaymentStatus.VOID;
            case "1001", "4001" -> PaymentStatus.COMPLETE;
            default -> PaymentStatus.PENDING;
        };
    }

    public String generateTrx(String provider) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        String dateStr = dateFormat.format(new Date());
        return txPrefix + provider + dateStr;
    }
}

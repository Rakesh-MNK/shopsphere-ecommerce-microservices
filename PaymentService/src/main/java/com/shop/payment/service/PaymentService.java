package com.shop.payment.service;

import com.shop.payment.client.InventoryClient;
import com.shop.payment.dto.PaymentEvent;
import com.shop.payment.dto.PaymentRequest;
import com.shop.payment.dto.PaymentResponse;
import com.shop.payment.dto.StockItem;
import com.shop.payment.entity.Payment;
import com.shop.payment.entity.PaymentStatus;
import com.shop.payment.event.PaymentEventPublisher;
import com.shop.payment.exception.ResourceNotFoundException;
import com.shop.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final InventoryClient inventoryClient;
    private final PaymentEventPublisher eventPublisher;

    /**
     * Simulated gateway: a card number ending in 0000 (or method FAIL) is declined, everything else succeeds.
     * SUCCESS -> inventory quantity minus (confirm), FAILED -> inventory quantity adding (release).
     */
    public PaymentResponse process(PaymentRequest request, String authorization,String mail) {
        Optional<Payment> existing = paymentRepository.findByOrderId(request.orderId());
        if (existing.isPresent()) {
            return PaymentResponse.from(existing.get(), "Payment already processed for this order");
        }

        boolean success = isApproved(request);
        for (StockItem item : request.items()) {
            if (success) {
                inventoryClient.confirm(item, authorization);
            } else {
                inventoryClient.release(item, authorization);
            }
        }

        Payment payment = new Payment();
        payment.setOrderId(request.orderId());
        payment.setUserId(request.userId());
        payment.setAmount(request.amount());
        payment.setMethod(request.method());
        payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setTransactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        payment.setFailureReason(success ? null : "Payment declined by issuer (simulated)");
        payment.setCreatedAt(LocalDateTime.now());
        Payment saved = paymentRepository.save(payment);

        eventPublisher.publish(new PaymentEvent(success ? "PAYMENT_SUCCESS" : "PAYMENT_FAILED", saved.getId(),
                saved.getOrderId(), saved.getUserId(), saved.getAmount(), saved.getStatus().name(),
                saved.getFailureReason(), LocalDateTime.now(),mail));
        log.info("Payment {} for order {} -> {}", saved.getId(), saved.getOrderId(), saved.getStatus());

        return PaymentResponse.from(saved, success ? "Payment successful" : saved.getFailureReason());
    }

    public PaymentResponse getByOrderId(Long orderId) {
        Payment p = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order " + orderId));
        return PaymentResponse.from(p, null);
    }

    public Long ownerOfOrderPayment(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order " + orderId))
                .getUserId();
    }

    public List<PaymentResponse> getByUser(Long userId) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(p -> PaymentResponse.from(p, null)).toList();
    }

    private boolean isApproved(PaymentRequest request) {
        if ("FAIL".equalsIgnoreCase(request.method())) {
            return false;
        }
        String card = request.cardNumber();
        return card == null || !card.trim().endsWith("0000");
    }
}

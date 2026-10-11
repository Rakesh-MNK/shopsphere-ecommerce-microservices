package com.shop.payment.service;

import com.shop.payment.client.InventoryClient;
import com.shop.payment.dto.PaymentEvent;
import com.shop.payment.dto.PaymentRequest;
import com.shop.payment.dto.PaymentResponse;
import com.shop.payment.dto.StockItem;
import com.shop.payment.entity.Payment;
import com.shop.payment.event.PaymentEventPublisher;
import com.shop.payment.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PaymentServiceTest {

  /*  @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private InventoryClient inventoryClient;
    @Mock
    private PaymentEventPublisher eventPublisher;
    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest request(String card) {
        return new PaymentRequest(100L, 1L, new BigDecimal("500.00"), "CARD", card,
                List.of(new StockItem(1L, 2)));
    }

    private void stubSave() {
        when(paymentRepository.findByOrderId(100L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(55L);
            return p;
        });
    }

    @Test
    void process_success_confirmsInventoryAndPublishesSuccessEvent() {
        stubSave();
        PaymentResponse response = paymentService.process(request("4111111111111111"), "Bearer t");

        assertEquals("SUCCESS", response.status());
        verify(inventoryClient).confirm(any(StockItem.class), any(String.class));
        verify(inventoryClient, never()).release(any(StockItem.class), any(String.class));
        ArgumentCaptor<PaymentEvent> captor = ArgumentCaptor.forClass(PaymentEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertEquals("PAYMENT_SUCCESS", captor.getValue().eventType());
    }

    @Test
    void process_declinedCard_releasesInventoryAndPublishesFailedEvent() {
        stubSave();
        PaymentResponse response = paymentService.process(request("4111111111110000"), "Bearer t");

        assertEquals("FAILED", response.status());
        verify(inventoryClient).release(any(StockItem.class), any(String.class));
        verify(inventoryClient, never()).confirm(any(StockItem.class), any(String.class));
        ArgumentCaptor<PaymentEvent> captor = ArgumentCaptor.forClass(PaymentEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertEquals("PAYMENT_FAILED", captor.getValue().eventType());
    } */
}

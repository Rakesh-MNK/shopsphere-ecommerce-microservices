package com.shop.order.service;

import com.shop.order.client.InventoryClient;
import com.shop.order.client.PaymentClient;
import com.shop.order.client.ProductClient;
import com.shop.order.dto.*;
import com.shop.order.entity.Order;
import com.shop.order.entity.OrderItem;
import com.shop.order.entity.OrderStatus;
import com.shop.order.event.OrderEventPublisher;
import com.shop.order.exception.BusinessException;
import com.shop.order.repository.OrderRepository;
import com.shop.order.security.AuthUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {

  /*  private static final String AUTH = "Bearer token";

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductClient productClient;
    @Mock
    private InventoryClient inventoryClient;
    @Mock
    private PaymentClient paymentClient;
    @Mock
    private OrderEventPublisher eventPublisher;
    @InjectMocks
    private OrderService orderService;

    private final CreateOrderRequest request = new CreateOrderRequest(
            List.of(new CreateOrderRequest.Item(1L, 2)), "CARD", "4111111111111111");

    @BeforeEach
    void setUp() {
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            if (o.getId() == null) {
                o.setId(100L);
            }
            return o;
        });
        when(productClient.getProduct(1L, AUTH)).thenReturn(new ProductDto(1L, "Laptop", new BigDecimal("500.00"), "Electronics"));
    }

    @Test
    void createOrder_paymentSuccess_orderConfirmed() {
        when(paymentClient.pay(any(PaymentRequest.class), anyString()))
                .thenReturn(new PaymentResponse(9L, 100L, "SUCCESS", "TXN-1", new BigDecimal("1000.00"), "ok"));

        OrderResponse response = orderService.createOrder(1L, request, AUTH);

        assertEquals("CONFIRMED", response.status());
        assertEquals(0, new BigDecimal("1000.00").compareTo(response.totalAmount()));
        verify(eventPublisher).publish(eq("ORDER_CONFIRMED"), any(Order.class));
        verify(inventoryClient, never()).release(any(StockRequest.class), anyString());
    }

    @Test
    void createOrder_paymentFailed_orderMarkedPaymentFailed() {
        when(paymentClient.pay(any(PaymentRequest.class), anyString()))
                .thenReturn(new PaymentResponse(9L, 100L, "FAILED", "TXN-1", new BigDecimal("1000.00"), "declined"));

        OrderResponse response = orderService.createOrder(1L, request, AUTH);

        assertEquals("PAYMENT_FAILED", response.status());
        verify(eventPublisher).publish(eq("ORDER_PAYMENT_FAILED"), any(Order.class));
    }

    @Test
    void createOrder_paymentServiceDown_releasesStockAndMarksFailed() {
        when(paymentClient.pay(any(PaymentRequest.class), anyString()))
                .thenThrow(new com.shop.order.exception.ServiceUnavailableException("payment-service down"));

        OrderResponse response = orderService.createOrder(1L, request, AUTH);

        assertEquals("PAYMENT_FAILED", response.status());
        assertEquals("PAYMENT_UNAVAILABLE", response.paymentStatus());
        verify(inventoryClient).release(any(StockRequest.class), anyString());
    }

    @Test
    void cancelOrder_otherUsersOrder_denied() {
        Order order = confirmedOrder(2L);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        assertThrows(AccessDeniedException.class,
                () -> orderService.cancelOrder(100L, new AuthUser(1L, "a@b.com", "CUSTOMER"), AUTH));
    }

    @Test
    void cancelOrder_notConfirmed_throwsBusinessException() {
        Order order = confirmedOrder(1L);
        order.setStatus(OrderStatus.PAYMENT_FAILED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        assertThrows(BusinessException.class,
                () -> orderService.cancelOrder(100L, new AuthUser(1L, "a@b.com", "CUSTOMER"), AUTH));
    }

    @Test
    void cancelOrder_confirmed_restocksAndCancels() {
        Order order = confirmedOrder(1L);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.cancelOrder(100L, new AuthUser(1L, "a@b.com", "CUSTOMER"), AUTH);

        assertEquals("CANCELLED", response.status());
        verify(inventoryClient).restock(any(StockRequest.class), anyString());
        verify(eventPublisher).publish(eq("ORDER_CANCELLED"), any(Order.class));
    }

    private Order confirmedOrder(Long userId) {
        Order order = new Order();
        order.setId(100L);
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(new BigDecimal("1000.00"));
        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setProductName("Laptop");
        item.setUnitPrice(new BigDecimal("500.00"));
        item.setQuantity(2);
        item.setSubtotal(new BigDecimal("1000.00"));
        order.addItem(item);
        return order;
    }

    private static <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    } */
}

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
import com.shop.order.exception.ResourceNotFoundException;
import com.shop.order.repository.OrderRepository;
import com.shop.order.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates: ProductService (price) -> InventoryService (reserve) -> PaymentService (pay) over OpenFeign.
 * Not wrapped in one DB transaction on purpose: remote calls must not hold a DB connection.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final OrderEventPublisher eventPublisher;

    public OrderResponse createOrder(Long userId, CreateOrderRequest request, String authorization, String mail) {
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        List<StockRequest> reserved = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (CreateOrderRequest.Item item : request.items()) {
                ProductDto product = productClient.getProduct(item.productId(), authorization);
                StockRequest stock = new StockRequest(item.productId(), item.quantity());
                inventoryClient.reserve(stock, authorization);
                reserved.add(stock);

                BigDecimal subtotal = product.price().multiply(BigDecimal.valueOf(item.quantity()));
                total = total.add(subtotal);

                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(product.id());
                orderItem.setProductName(product.name());
                orderItem.setUnitPrice(product.price());
                orderItem.setQuantity(item.quantity());
                orderItem.setSubtotal(subtotal);
                order.addItem(orderItem);
            }
        } catch (RuntimeException ex) {
            releaseQuietly(reserved, authorization);   // compensate the items already reserved
            throw ex;
        }

        order.setTotalAmount(total);
        order = orderRepository.save(order);
        eventPublisher.publish("ORDER_CREATED", order);

        PaymentResponse payment;
        try {
            payment = paymentClient.pay(new PaymentRequest(order.getId(), userId, total,
                    request.paymentMethod(), request.cardNumber(), reserved), authorization,mail);
        } catch (RuntimeException ex) {
            log.error("Payment call failed for order {}: {}", order.getId(), ex.getMessage());
            releaseQuietly(reserved, authorization);
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            order.setPaymentStatus("PAYMENT_UNAVAILABLE");
            order.setUpdatedAt(LocalDateTime.now());
            order = orderRepository.save(order);
            eventPublisher.publish("ORDER_PAYMENT_FAILED", order);
            return OrderResponse.from(order);
        }

        boolean success = "SUCCESS".equals(payment.status());
        order.setPaymentId(payment.paymentId());
        order.setPaymentStatus(payment.status());
        order.setStatus(success ? OrderStatus.CONFIRMED : OrderStatus.PAYMENT_FAILED);
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);
        eventPublisher.publish(success ? "ORDER_CONFIRMED" : "ORDER_PAYMENT_FAILED", order);
        return OrderResponse.from(order);
    }

    public OrderResponse cancelOrder(Long orderId, AuthUser user, String authorization) {
        Order order = findOrThrow(orderId);
        if (!order.getUserId().equals(user.userId())) {
            throw new AccessDeniedException("You can cancel only your own orders");
        }
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new BusinessException("Only CONFIRMED orders can be cancelled. Current status: " + order.getStatus());
        }
        for (OrderItem item : order.getItems()) {
            inventoryClient.restock(new StockRequest(item.getProductId(), item.getQuantity()), authorization);
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);
        eventPublisher.publish("ORDER_CANCELLED", order);
        return OrderResponse.from(order);
    }

    public OrderResponse getOrder(Long orderId, AuthUser user) {
        Order order = findOrThrow(orderId);
        if (!"ADMIN".equals(user.role()) && !order.getUserId().equals(user.userId())) {
            throw new AccessDeniedException("You can view only your own orders");
        }
        return OrderResponse.from(order);
    }

    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(OrderResponse::from).toList();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(OrderResponse::from).toList();
    }

    private Order findOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    private void releaseQuietly(List<StockRequest> reserved, String authorization) {
        for (StockRequest stock : reserved) {
            try {
                inventoryClient.release(stock, authorization);
            } catch (RuntimeException e) {
                log.error("Compensation failed: could not release product {} qty {}: {}",
                        stock.productId(), stock.quantity(), e.getMessage());
            }
        }
    }
}

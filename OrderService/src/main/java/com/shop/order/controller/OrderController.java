package com.shop.order.controller;

import com.shop.order.dto.CreateOrderRequest;
import com.shop.order.dto.OrderResponse;
import com.shop.order.security.AuthUser;
import com.shop.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request,
                                                @AuthenticationPrincipal AuthUser user,
                                                @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(user.userId(), request, authorization,user.email()));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderResponse cancel(@PathVariable Long id,
                                @AuthenticationPrincipal AuthUser user,
                                @RequestHeader("Authorization") String authorization) {
        return orderService.cancelOrder(id, user, authorization);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<OrderResponse> myOrders(@AuthenticationPrincipal AuthUser user) {
        return orderService.getMyOrders(user.userId());
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id, @AuthenticationPrincipal AuthUser user) {
        return orderService.getOrder(id, user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> all() {
        return orderService.getAllOrders();
    }
}

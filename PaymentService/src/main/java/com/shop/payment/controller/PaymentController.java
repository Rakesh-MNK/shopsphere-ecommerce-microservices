package com.shop.payment.controller;

import com.shop.payment.dto.PaymentRequest;
import com.shop.payment.dto.PaymentResponse;
import com.shop.payment.security.AuthUser;
import com.shop.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /** Called by OrderService through OpenFeign (customer's JWT is forwarded). */
    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<PaymentResponse> pay(@Valid @RequestBody PaymentRequest request,
                                               @RequestHeader("Authorization") String authorization,
                                               @RequestParam String mail) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.process(request, authorization,mail));
    }

    @GetMapping("/order/{orderId}")
    public PaymentResponse getByOrder(@PathVariable Long orderId, @AuthenticationPrincipal AuthUser user) {
        if (!"ADMIN".equals(user.role()) && !user.userId().equals(paymentService.ownerOfOrderPayment(orderId))) {
            throw new AccessDeniedException("Not your payment");
        }
        return paymentService.getByOrderId(orderId);
    }

    @GetMapping("/my")
    public List<PaymentResponse> myPayments(@AuthenticationPrincipal AuthUser user) {
        return paymentService.getByUser(user.userId());
    }
}

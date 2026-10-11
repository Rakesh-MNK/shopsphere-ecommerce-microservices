package com.shop.notification.service;

import com.shop.notification.dto.LowStockEvent;
import com.shop.notification.dto.NotificationResponse;
import com.shop.notification.dto.OrderEvent;
import com.shop.notification.dto.PaymentEvent;
import com.shop.notification.entity.Notification;
import com.shop.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

	private final NotificationRepository notificationRepository;

	private final EmailService emailService;

	public void handleOrderEvent(OrderEvent e) {
		String message = switch (e.eventType()) {
		case "ORDER_CREATED" -> "Your order #%d has been placed. Total: %s".formatted(e.orderId(), e.totalAmount());
		case "ORDER_CONFIRMED" -> "Your order #%d is confirmed. Thank you for shopping!".formatted(e.orderId());
		case "ORDER_PAYMENT_FAILED" -> "Payment failed for order #%d. Your items were released.".formatted(e.orderId());
		case "ORDER_CANCELLED" -> "Your order #%d has been cancelled.".formatted(e.orderId());
		default -> "Order #%d update: %s".formatted(e.orderId(), e.status());
		};
		save(e.userId(), "CUSTOMER", e.eventType(), "Order update #" + e.orderId(), message, "ORDER-" + e.orderId());
	}

	public void handlePaymentEvent(PaymentEvent e) {
		String message = "PAYMENT_SUCCESS".equals(e.eventType())
				? "Payment of %s received for order #%d.".formatted(e.amount(), e.orderId())
				: "Payment of %s for order #%d failed: %s".formatted(e.amount(), e.orderId(), e.reason());

		savePayment(e.userId(), "CUSTOMER", e.eventType(), e.eventType() + " for order #" + e.orderId(), message,
				"PAYMENT-" + e.paymentId(), e.mail());

	}

	public void handleLowStockEvent(LowStockEvent e) {
		String message = "Low stock alert: '%s' (product %d) has only %d units left (threshold %d)."
				.formatted(e.productName(), e.productId(), e.availableQty(), e.threshold());
		save(null, "ADMIN", e.eventType(), "Low stock: " + e.productName(), message, "PRODUCT-" + e.productId());
	}

	public List<NotificationResponse> getForUser(Long userId) {
		return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId).stream()
				.map(NotificationResponse::from).toList();
	}

	public List<NotificationResponse> getAll() {
		return notificationRepository.findAllByOrderByCreatedAtDesc().stream().map(NotificationResponse::from).toList();
	}

	public List<NotificationResponse> getAdminAlerts() {
		return notificationRepository.findByRecipientRoleOrderByCreatedAtDesc("ADMIN").stream()
				.map(NotificationResponse::from).toList();
	}

	private void save(Long userId, String role, String type, String subject, String message, String ref) {
		Notification n = new Notification();
		n.setRecipientUserId(userId);
		n.setRecipientRole(role);
		n.setType(type);
		n.setChannel("EMAIL"); // simulated channel: logged + stored
		n.setSubject(subject);
		n.setMessage(message);
		n.setReferenceId(ref);
		n.setCreatedAt(LocalDateTime.now());
		notificationRepository.save(n);
		log.info("[EMAIL -> {}] {} | {}", userId == null ? "ADMIN" : "user-" + userId, subject, message);
	}

	private void savePayment(Long userId, String role, String type, String subject, String message, String ref,
			String mail) {
		
		Notification n = new Notification();

		if ("PAYMENT_SUCCESS".equalsIgnoreCase(type)) {

			subject = "Order Confirmed " + subject;
			n.setType(type);
			n.setSubject(subject);
			n.setMessage(message);

			String emessage = """


					Thank you for shopping with us!

					message: %s

					Your order will be processed shortly.
					""".formatted(message);
			emailService.sendSimpleEmail(mail, type, subject, emessage);

		} else if ("PAYMENT_FAILURE".equalsIgnoreCase(type) || "PAYMENT_FAILED".equalsIgnoreCase(type)) {

			n.setType(type);
			n.setSubject(subject);
			n.setMessage(message);

			String emessage = """

					Unfortunately, your payment could not be completed.

					message : %s

					         """.formatted(message);
			emailService.sendSimpleEmail(mail, type, subject, emessage);

		} else {
			throw new IllegalArgumentException("Unsupported payment status: ");

		}

		n.setRecipientUserId(userId);
		n.setRecipientRole(role);
		n.setChannel(mail); // simulated channel: logged + stored
		n.setReferenceId(ref);
		n.setCreatedAt(LocalDateTime.now());
		notificationRepository.save(n);
		log.info("[EMAIL -> {}] {} | {}", userId == null ? "ADMIN" : "user-" + userId, subject, message);

		

	}
}

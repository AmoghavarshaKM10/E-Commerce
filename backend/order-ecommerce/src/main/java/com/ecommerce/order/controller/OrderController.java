package com.ecommerce.order.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.order.config.PayPalPaymentService;
import com.ecommerce.order.model.CheckOutRequest;
import com.ecommerce.order.model.OrderDto;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.model.PaymentResponse;
import com.ecommerce.order.service.NotificationService;
import com.ecommerce.order.service.OrderServiceImpl;
import com.ecommerce.parent.config.AuditService;
import com.ecommerce.parent.config.PaymentException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author amoghavarshakm
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class OrderController {

	private final OrderServiceImpl orderService;
	private final AuditService auditService;
	private final PayPalPaymentService payPalPaymentService;
	private final NotificationService notifyService;

	/**
	 * 
	 * @param userId
	 * @return List<Orders> for the user.
	 */
	@GetMapping("/all")
	public ResponseEntity<List<OrderDto>> getOrders(@RequestAttribute("X-User-Id") Long userId) {
		log.info("getOrders api entry");
		List<OrderDto> orders = orderService.getUserOrdersWithItems(userId);
		log.info("getOrders api exit");
		return ResponseEntity.ok(orders);
	}

	/**
	 * 
	 * @param request
	 * @param userId
	 * @param username
	 * @return new Order.
	 */
	@PostMapping("/checkout")
	public ResponseEntity<OrderDto> checkout(@Valid @RequestBody CheckOutRequest request,
			@RequestAttribute("X-User-Id") Long userId, @RequestAttribute("X-Username") String username) {
		log.info("checkout api entry");
		OrderDto order = orderService.checkout(userId, username, request);
		log.info("checkout api exit");
		return ResponseEntity.ok(order);
	}

	/**
	 * 
	 * @param orderId
	 * @param userId
	 * @return cancelled Order.
	 */
	@PutMapping("/{orderId}/cancel")
	public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long orderId, @RequestAttribute("X-User-Id") Long userId) {
		log.info("cancelOrder api entry");
		OrderDto order = orderService.cancelOrder(orderId, userId);
		Map<String,String> notifyMap = new HashMap<>();
		notifyMap.put("orderId", orderId.toString());
		notifyMap.put("event", "ORDER_CANCELLED");
		notifyService.sendNotification(notifyMap);
		log.info("cancelOrder api exit");
		return ResponseEntity.ok(order);
	}

	/**
	 * This api will be used my Admin and Internal Purpose only.
	 * 
	 * @param orderId
	 * @param userId
	 * @return order Details
	 */
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderDto> getOrder(@PathVariable Long orderId, @RequestAttribute("X-User-Id") Long userId) {
		log.info("getOrder api entry for OrderId:{}", orderId);
		OrderDto order = orderService.getOrder(orderId, userId);
		log.info("getOrder api exit for OrderId:{}", orderId);
		return ResponseEntity.ok(order);
	}

	/**
	 * This api will be used my Admin and Internal Purpose only.
	 * 
	 * @param orderId
	 * @param status
	 * @return order Details
	 */
	@PutMapping("/{orderId}/status")
	public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long orderId, @RequestParam String status) {
		log.info("updateOrderStatus api entry for OrderId:{}", orderId);
		OrderDto order = orderService.updateOrderStatus(orderId, status, null, null);
		log.info("updateOrderStatus api exit for OrderId:{}", orderId);
		return ResponseEntity.ok(order);
	}

	/**
	 * This api will be used for Paypal capture.
	 * 
	 * @param token
	 * @return paymentStatus
	 */
	@GetMapping("/payments/success")
	public ResponseEntity<?> paymentSuccess(@RequestParam String token) {
		log.info("PayPal redirect success with paymentId: {}", token);

		try {
			PaymentResponse captureResponse = payPalPaymentService.capturePayment(token);

			if (captureResponse.isSuccess()) {
				orderService.updateOrderStatusByPaymentId(token, OrderStatus.CONFIRMED.toString(), "COMPLETED");
				Map<String,Object> response =  new HashMap<>();
				response.put("success", true);
				response.put("paymentId", captureResponse.getPaymentId());
				response.put("message", "Payment completed successfully");
				
				Map<String,String> notifyMap = new HashMap<>();
				notifyMap.put("paymentId", token);
				notifyMap.put("event", "PAYMENT_SUCCESS");
				notifyService.sendNotification(notifyMap);

				return ResponseEntity.ok("Payment Success");
			} else {
				throw new PaymentException("Capture failed", "CAPTURE_FAILED");
			}

		} catch (Exception e) {
			log.error("Payment capture failed for token: {}", token, e);

			orderService.updateOrderStatusByPaymentId(token, OrderStatus.FAILED.toString(), "FAILED");

			Map<String,Object> errorResponse =  new HashMap<>();
			errorResponse.put("success", false);
			errorResponse.put("error", "Payment failed: " + e.getMessage());
			
			Map<String,String> notifyMap = new HashMap<>();
			notifyMap.put("paymentId", token);
			notifyMap.put("event", "PAYMENT_FAILED");
			notifyService.sendNotification(notifyMap);

			return null;
		}
	}

	/**
	 * 
	 * @param token
	 * @return message
	 */
	@GetMapping("/payments/cancel")
	public ResponseEntity<?> paymentCancel(@RequestParam String token) {
		log.info("PayPal payment cancelled for paymentId: {}", token);
		orderService.updateOrderStatusByPaymentId(token, OrderStatus.CANCELLED.toString(), "CANCELLED");
		Map<String,Object> response =  new HashMap<>();
		response.put("success", false);
		response.put("message", "Payment was cancelled");
		
		Map<String,String> notifyMap = new HashMap<>();
		notifyMap.put("paymentId", token);
		notifyMap.put("event", "PAYMENT_CANCEL");
		notifyService.sendNotification(notifyMap);

		return ResponseEntity.ok("Payment Cancelled");
	}
	
	/**
	 * 
	 * @param paymentId
	 * @return paymentStatus
	 */
	@GetMapping("/payments/status")
	public ResponseEntity<?> checkPaymentStatus(@RequestParam String paymentId){
		log.info("checkPaymentStatus api entry for paymentId:{}",paymentId);
		PaymentResponse response = payPalPaymentService.getPaymentStatus(paymentId);
		return ResponseEntity.ok(response);
	}

}
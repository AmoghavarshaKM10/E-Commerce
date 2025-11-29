package com.ecommerce.order.config;

import java.util.Base64;
import java.util.Collections;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecommerce.order.model.PaymentRequest;
import com.ecommerce.order.model.PaymentResponse;
import com.ecommerce.parent.config.PaymentException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

/**
 * @author amoghavarshakm
 */
@Service
@Slf4j
public class PayPalPaymentService {

	@Value("${paypal.client-id:test}")
	private String clientId;

	@Value("${paypal.client-secret:test}")
	private String clientSecret;

	@Value("${paypal.mode:sandbox}")
	private String mode;

	@Value("${paypal.return.url}")
	private String returnUrl;

	@Value("${paypal.cancel.url}")
	private String cancelUrl;

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	public PayPalPaymentService(ObjectMapper objectMapper) {
		this.restTemplate = new RestTemplate();
		this.objectMapper = objectMapper;
	}

	private String getBaseUrl() {
		return "sandbox".equals(mode) ? "https://api-m.sandbox.paypal.com" : "https://api-m.paypal.com";
	}

	private String getAccessToken() {
		try {
			String auth = clientId + ":" + clientSecret;
			String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.set("Authorization", "Basic " + encodedAuth);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			String body = "grant_type=client_credentials";

			HttpEntity<String> request = new HttpEntity<>(body, headers);

			ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "/v1/oauth2/token", request,
					String.class);

			if (response.getStatusCode() != HttpStatus.OK) {
				throw new PaymentException("Failed to get PayPal access token", "PAYPAL_AUTH_ERROR");
			}

			JsonNode jsonResponse = objectMapper.readTree(response.getBody());
			return jsonResponse.get("access_token").asText();

		} catch (Exception e) {
			log.error("Failed to get PayPal access token", e);
			throw new PaymentException("Failed to authenticate with PayPal: " + e.getMessage(), "PAYPAL_AUTH_ERROR");
		}
	}

	public PaymentResponse processPayment(PaymentRequest request) {
		log.info("Processing PayPal payment for order: {}", request.getOrderId());
		try {
			ObjectNode orderResponse = createPayPalOrderWithLinks(request);

			String paymentId = orderResponse.get("id").asText();
			String status = orderResponse.get("status").asText();

			String approvalUrl = extractApprovalUrl(orderResponse);

			log.info("PayPal order created: {} with status: {}. Approval URL: {}", paymentId, status, approvalUrl);

			if ("CREATED".equals(status) || "APPROVED".equals(status)) {
				return new PaymentResponse(false, paymentId, "Payment requires user approval", "PENDING", approvalUrl);
			} else {
				throw new PaymentException("Unexpected order status: " + status, "UNEXPECTED_STATUS");
			}

		} catch (Exception e) {
			log.error("PayPal payment failed for order: {}", request.getOrderId(), e);
			throw new PaymentException("PayPal payment failed: " + e.getMessage(), "PAYPAL_ERROR");
		}
	}

	private ObjectNode createPayPalOrderWithLinks(PaymentRequest request) {
		String accessToken = getAccessToken();

		ObjectNode orderRequest = objectMapper.createObjectNode();
		orderRequest.put("intent", "CAPTURE");

		ObjectNode applicationContext = objectMapper.createObjectNode();
		applicationContext.put("return_url", returnUrl);
		applicationContext.put("cancel_url", cancelUrl);
		applicationContext.put("brand_name", "E-Commerce Store");
		applicationContext.put("user_action", "PAY_NOW");
		orderRequest.set("application_context", applicationContext);

		ObjectNode amount = objectMapper.createObjectNode();
		amount.put("currency_code", "USD");
		amount.put("value", request.getAmount().toString());

		ObjectNode purchaseUnit = objectMapper.createObjectNode();
		purchaseUnit.set("amount", amount);
		purchaseUnit.put("reference_id", request.getOrderId().toString());
		purchaseUnit.put("description", "Order #" + request.getOrderId());

		ArrayNode purchaseUnits = objectMapper.createArrayNode();
		purchaseUnits.add(purchaseUnit);
		orderRequest.set("purchase_units", purchaseUnits);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + accessToken);
		headers.set("PayPal-Request-Id", "order-" + request.getOrderId());
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		HttpEntity<String> entity = new HttpEntity<>(orderRequest.toString(), headers);

		ResponseEntity<String> response = restTemplate.exchange(getBaseUrl() + "/v2/checkout/orders", HttpMethod.POST,
				entity, String.class);

		if (response.getStatusCode() != HttpStatus.CREATED && response.getStatusCode() != HttpStatus.OK) {
			throw new PaymentException("Failed to create PayPal payment: " + response.getStatusCode(),
					"PAYMENT_CREATION_FAILED");
		}

		try {
			return (ObjectNode) objectMapper.readTree(response.getBody());
		} catch (Exception e) {
			throw new PaymentException("Failed to parse PayPal response", "RESPONSE_PARSE_ERROR");
		}
	}

	private String extractApprovalUrl(ObjectNode orderResponse) {
		JsonNode links = orderResponse.get("links");
		if (links != null && links.isArray()) {
			for (JsonNode link : links) {
				if ("approve".equals(link.get("rel").asText())) {
					return link.get("href").asText();
				}
			}
		}
		throw new PaymentException("No approval URL found in PayPal response", "NO_APPROVAL_URL");
	}

	public PaymentResponse capturePayment(String paymentId) {
		log.info("Capturing PayPal payment for paymentId: {}", paymentId);
		String accessToken = getAccessToken();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + accessToken);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		HttpEntity<String> entity = new HttpEntity<>("{}", headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(
					getBaseUrl() + "/v2/checkout/orders/" + paymentId + "/capture", HttpMethod.POST, entity,
					String.class);

			if (response.getStatusCode() != HttpStatus.CREATED && response.getStatusCode() != HttpStatus.OK) {
				throw new PaymentException("Failed to capture payment: " + response.getStatusCode(), "CAPTURE_FAILED");
			}

			ObjectNode jsonResponse = (ObjectNode) objectMapper.readTree(response.getBody());
			String status = jsonResponse.get("status").asText();

			if ("COMPLETED".equals(status)) {
				log.info("PayPal payment captured successfully. PaymentId: {}", paymentId);
				return new PaymentResponse(true, paymentId, "Payment successful", "COMPLETED", null);
			} else {
				throw new PaymentException("Payment not completed. Status: " + status, "PAYMENT_INCOMPLETE");
			}

		} catch (Exception e) {
			log.error("PayPal capture failed for paymentID: {}", paymentId, e);
			throw new PaymentException("Failed to capture payment: " + e.getMessage(), "CAPTURE_FAILED");
		}
	}

	public PaymentResponse getPaymentStatus(String paymentId) {
		try {
			String accessToken = getAccessToken();

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + accessToken);
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

			HttpEntity<String> entity = new HttpEntity<>(headers);

			ResponseEntity<String> response = restTemplate.exchange(getBaseUrl() + "/v2/checkout/orders/" + paymentId,
					HttpMethod.GET, entity, String.class);

			ObjectNode jsonResponse = (ObjectNode) objectMapper.readTree(response.getBody());
			String status = jsonResponse.get("status").asText();

			boolean success = "COMPLETED".equals(status);
			String message = "Payment status: " + status;

			return new PaymentResponse(success, paymentId, message, status, null);

		} catch (Exception e) {
			log.error("Failed to get PayPal payment status: {}", paymentId, e);
			throw new PaymentException("Failed to fetch payment status: " + e.getMessage(), "STATUS_CHECK_FAILED");
		}
	}
}
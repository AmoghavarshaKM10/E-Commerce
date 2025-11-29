package com.ecommerce.order.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ecommerce.order.config.NotificationProducerConfig;
import com.ecommerce.order.model.OrderEntity;
import com.ecommerce.order.model.OrderNotificationDto;
import com.ecommerce.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author amoghavarshakm
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationProducerConfig producerConfig;
	private final OrderRepository repo;

	/**
	 * 
	 * @param notificationMap
	 */
	@Async
	public void sendNotification(Map<String, String> notificationMap) {
		log.info("sendNotification method entry for event Type:{}", notificationMap.get("event"));
		String eventType = notificationMap.get("event");

		OrderEntity entity = "ORDER_CANCELLED".equalsIgnoreCase(eventType)
				? repo.findById(Long.parseLong(notificationMap.get("orderId")))
						.orElseThrow(() -> new RuntimeException("Order not found"))
				: repo.findByPaymentId(notificationMap.get("paymentId"))
						.orElseThrow(() -> new RuntimeException("Order not found"));

		OrderNotificationDto notifyDto = new OrderNotificationDto();
		notifyDto.setEventType(eventType);
		notifyDto.setOrderId(entity.getOrderId());
		notifyDto.setOrderStatus(entity.getStatus());
		notifyDto.setPaymentId(entity.getPaymentId());
		notifyDto.setPaymentStatus(entity.getPaymentStatus());
		notifyDto.setUserId(entity.getUserId());
		notifyDto.setTotalAmount(entity.getTotalAmount());
		notifyDto.setTimestamp(LocalDateTime.now());

		producerConfig.sendNotification(notifyDto);
		log.info("sendNotification method exit for event Type:{}", notificationMap.get("event"));
	}
}



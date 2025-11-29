package com.ecommerce.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.order.model.OrderNotificationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author amoghavarshakm
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProducerConfig {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	@Value("${kafka.topics.notifications}")
	private String notificationsTopic;

	public void sendNotification(OrderNotificationDto notification) {
		try {
			log.info("sendNotification method entry");
			String message = objectMapper.writeValueAsString(notification);
			String key = notification.getOrderId().toString();

			kafkaTemplate.send(notificationsTopic, key, message)
			.whenComplete((result, ex) -> {
				if (ex == null) {
					log.info("Notification sent. Event: {}, Order: {}, Partition: {}", notification.getEventType(),
							notification.getOrderId(), result.getRecordMetadata().partition());
				} else {
					log.error(" Failed to send notification. Event: {}, Order: {}, Error: {}",
							notification.getEventType(), notification.getOrderId(), ex.getMessage());
				}
			});

		} catch (JsonProcessingException e) {
			log.error("Error serializing notification. Order: {}", notification.getOrderId(), e);
			throw new RuntimeException("Failed to serialize notification", e);
		}
	}
}

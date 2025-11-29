package com.ecommerce.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderNotificationDto {

	private String eventType; 
    private Long orderId;
    private Long userId;
    private String orderStatus;
    private String paymentStatus;
    private String paymentId;
    private BigDecimal totalAmount;
    private LocalDateTime timestamp;
}

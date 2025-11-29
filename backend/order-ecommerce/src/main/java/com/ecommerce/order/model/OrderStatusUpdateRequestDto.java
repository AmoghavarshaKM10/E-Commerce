package com.ecommerce.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author amoghavarshakm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequestDto {
	
	private Long userId;
    private Long orderId;
    private String status;
}
package com.ecommerce.order.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author amoghavarshakm
 */
@Data
public class PaymentRequest {

	private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String method;
    private String email;
    private String contact;
}

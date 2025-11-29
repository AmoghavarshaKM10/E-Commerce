package com.ecommerce.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author amoghavarshakm
 */
@Data
@AllArgsConstructor
public class PaymentResponse {

	private boolean success;
    private String paymentId;
    private String message;
    private String status;
    private String approvalUrl; 

}

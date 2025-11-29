package com.ecommerce.order.model;

import lombok.Data;

/**
 * @author amoghavarshakm
 */
@Data
public class CardDetails {

	private String number;
    private String name;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
}

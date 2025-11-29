package com.ecommerce.cart.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author amoghavarshakm
 */
@Data
public class AddToCartRequest {
	 	private Long productId;
	    private String productName;
	    private Integer quantity;
	    private BigDecimal unitPrice;
}

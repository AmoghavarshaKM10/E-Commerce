package com.ecommerce.cart.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * @author amoghavarshakm
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemDto {
	private Long cartId; 
	private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}

package com.ecommerce.cart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

/**
 * @author amoghavarshakm
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartDto {
	private Long cartId;
	private Long userId;
    private String username;
    private List<CartItemDto> items = new ArrayList<>();
    private LocalDateTime createdDate;
    
    public BigDecimal getTotalCartPrice() {
        return items.stream()
            .map(CartItemDto::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public int getTotalItemsCount() {
        return items.stream().mapToInt(CartItemDto::getQuantity).sum();
    }
}

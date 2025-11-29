package com.ecommerce.order.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author amoghavarshakm
 */
@Data
public class CheckOutRequest {

	@NotNull 
    private List<CartItemDto> cartItems;
	
    @NotBlank
    private String shippingAddress;
    
    @NotBlank
    private String paymentMethod;
    
    @NotBlank
    private String contact; 
}

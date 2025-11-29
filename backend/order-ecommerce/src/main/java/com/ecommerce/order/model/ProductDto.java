package com.ecommerce.order.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

/**
 * @author amoghavarshakm
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {

	private Long productId;
    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
    private String category;
    private String imageUrl;
	
}

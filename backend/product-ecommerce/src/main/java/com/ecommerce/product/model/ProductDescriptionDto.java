package com.ecommerce.product.model;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author amoghavarshakm
 */
@Data
public class ProductDescriptionDto {

	private Long productId;
    private String description;
    private BigDecimal ratings;
    private Integer returnPeriod;
    private Integer warrantyPeriod;
    private int reviewsCount;
}

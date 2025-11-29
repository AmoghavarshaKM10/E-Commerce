package com.ecommerce.product.model;

import java.util.List;

import lombok.Data;

/**
 * @author amoghavarshakm
 */
@Data
public class ProductIdRequestDto {

	private List<Long> productIds;
}

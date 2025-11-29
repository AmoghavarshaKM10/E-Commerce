package com.ecommerce.product.service;

import java.math.BigDecimal;
import java.util.List;

import com.ecommerce.product.model.ProductDescriptionDto;
import com.ecommerce.product.model.ProductDto;

/**
 * @author amoghavarshakm
 */

public interface ProductService {

	/**
	 * 
	 * @return Products
	 */
	List<ProductDto> getAllProducts();
	
	/**
	 * 
	 * @param productId
	 * @return details
	 */
	ProductDescriptionDto getProductDescription(Long productId);
	
	/**
	 * 
	 * @param id
	 * @param quantity
	 */
    void reduceStock(Long id, Integer quantity);
    
    /**
     * 
     * @param id
     * @param quantity
     */
    void updateStock(Long id, Integer quantity);
    
    /**
     * 
     * @param id
     * @param rating
     */
    void updateProductRating(Long id, BigDecimal rating);
    
    /**
     * 
     * @param productIds
     * @return stockDetails
     */
    List<ProductDto> getStockDetails(List<Long> productIds);
}

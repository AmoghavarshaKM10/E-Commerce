package com.ecommerce.cart.service;

import com.ecommerce.cart.model.AddToCartRequest;
import com.ecommerce.cart.model.CartDto;

/**
 * @author amoghavarshakm
 */
public interface CartService {
	
	/**
	 * 
	 * @param userId
	 * @param username
	 * @param request
	 * @return updatedCart
	 */
	CartDto addToCart(Long userId, String username, AddToCartRequest request);
	
	/**
	 * 
	 * @param userId
	 * @param username
	 * @param productId
	 * @param quantity
	 * @return updateQuantity
	 */
	CartDto updateQuantity(Long userId, String username, Long productId, Integer quantity);
	
	/**
	 * 
	 * @param userId
	 * @param username
	 * @param productId
	 * @return updatedCart
	 */
	CartDto removeItem(Long userId, String username, Long productId);
	
	
	/**
	 * 
	 * @param userId
	 */
	void syncCartToDatabase(Long userId) ;
}

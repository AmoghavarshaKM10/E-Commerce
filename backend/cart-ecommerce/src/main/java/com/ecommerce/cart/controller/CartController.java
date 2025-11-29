package com.ecommerce.cart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.cart.model.AddToCartRequest;
import com.ecommerce.cart.model.CartDto;
import com.ecommerce.cart.service.CartCacheService;
import com.ecommerce.cart.service.CartService;
import com.ecommerce.parent.config.AuditService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author amoghavarshakm
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CartController {
	
	private final CartService cartService;
	private final AuditService auditService;
	private final CartCacheService cacheService;

	/**
	 * 
	 * @param userId
	 * @param username
	 * @return cart
	 */
	@GetMapping("/all")
    public ResponseEntity<CartDto> getCart(
    		@RequestAttribute("X-User-Id") Long userId) {
		log.info("getCart api entry");
		String username = "test";
        return ResponseEntity.ok(cacheService.getCart(userId, username));
    }
    
	/**
	 * 
	 * @param userId
	 * @param username
	 * @param request
	 * @return cart
	 */
    @PostMapping("/items")
    public ResponseEntity<CartDto> addToCart(
    		@RequestAttribute("X-User-Id") Long userId,
           // @RequestHeader("X-Username") String username,
            @RequestBody AddToCartRequest request) {
    	log.info("addToCart api entry");
    	String username = "test";
        return ResponseEntity.ok(cartService.addToCart(userId, username, request));
    }
    
    /**
     * 
     * @param userId
     * @param username
     * @param productId
     * @param quantity
     * @return updatedCart
     */
    @PutMapping("/items/{productId}")
    public ResponseEntity<CartDto> updateQuantity(
    		@RequestAttribute("X-User-Id") Long userId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
    	String userName = "test";
    	log.info("updateQuantity api entry");
        return ResponseEntity.ok(cartService.updateQuantity(userId, userName, productId, quantity));
    }
    
    /**
     * 
     * @param userId
     * @param username
     * @param productId
     * @return updatedCart
     */
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartDto> removeItem(
    		@RequestAttribute("X-User-Id") Long userId,
           
            @PathVariable Long productId) {
    	String userName = "test";
    	log.info("removeItem api entry");
        return ResponseEntity.ok(cartService.removeItem(userId, userName, productId));
    }
    
    /**
     * 
     * @param userId
     * 
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestAttribute("X-User-Id") Long userId) {
        log.info("clearCart api entry");
        cacheService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
    
}
	


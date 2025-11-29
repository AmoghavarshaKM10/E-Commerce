package com.ecommerce.cart.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ecommerce.cart.model.AddToCartRequest;
import com.ecommerce.cart.model.CartDto;
import com.ecommerce.cart.model.CartEntity;
import com.ecommerce.cart.model.CartItemDto;
import com.ecommerce.cart.model.CartItemEntity;
import com.ecommerce.cart.repository.CartItemRepo;
import com.ecommerce.cart.repository.CartRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

	private final CartRepo cartRepository;
	private final CartItemRepo cartItemRepository;
	private final CartCacheService cacheService;
	private final RedisTemplate<String, Object> redisTemplate;
	private static final String CACHE_NAME = "cart";
	
	/**
	 * @param userId
	 */
	@CachePut(value = CACHE_NAME, key = "#userId")
	public CartDto addToCart(Long userId, String username, AddToCartRequest request) {
		log.info("addToCart service entry()");
	    CartDto cart = cacheService.getCart(userId, username);
	    Long cartId = cart.getCartId();
	    if (cartId == null) {
	    	log.info("no cart Found for User");
	        cartId = ensureCartExistsInDB(userId, username);
	        cart.setCartId(cartId);
	    }

	    Optional<CartItemDto> existingItem = cart.getItems().stream()
	            .filter(item -> item.getProductId().equals(request.getProductId()))
	            .findFirst();

	    if (existingItem.isPresent()) {
	        CartItemDto item = existingItem.get();
	        item.setQuantity(item.getQuantity() + request.getQuantity());
	        item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
	    } else {
	        CartItemDto newItem = new CartItemDto();
	        newItem.setCartId(cartId);
	        newItem.setProductId(request.getProductId());
	        newItem.setProductName(request.getProductName());
	        newItem.setQuantity(request.getQuantity());
	        newItem.setUnitPrice(request.getUnitPrice());
	        newItem.setTotalPrice(request.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
	        
	        cart.getItems().add(newItem);
	    }
	    log.info("addToCart service exit()");
	    return cart;
	}

	@CachePut(value = CACHE_NAME, key = "#userId")
	public CartDto updateQuantity(Long userId, String username, Long productId, Integer quantity) {
		log.info("updateQuantity method entry()");
		if (quantity <= 0) {
			return removeItem(userId, username, productId);
		}

		CartDto cart = cacheService.getCart(userId, username);
		cart.getItems().stream().filter(item -> item.getProductId().equals(productId)).findFirst().ifPresent(item -> {
			item.setQuantity(quantity);
			item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
		});

		log.info("updateQuantity method exit()");
		return cart;
	}

	/**
	 * @param userId
	 * @param userName
	 * @param productId
	 * @return updatedCart
	 */
	@CachePut(value = CACHE_NAME, key = "#userId", unless = "#result == null")
	public CartDto removeItem(Long userId, String username, Long productId) {
		log.info("removeItem method entry()");
		CartDto cart = cacheService.getCart(userId, username);
		boolean removed = cart.getItems().removeIf(item -> item.getProductId().equals(productId));

		if (removed && cart.getItems().isEmpty()) {
			log.info("cart is empty deleting from DB");
			cartRepository.deleteByUserId(userId);
			 CartDto emptyCart = new CartDto();
		        emptyCart.setUserId(userId);
		        emptyCart.setUsername(username);
		        emptyCart.setItems(new ArrayList<>());
		        return emptyCart;
		}
		log.info("removeItem method exit()");
		return cart;
	}

	/**
	 * 
	 * @param userId
	 * @param username
	 * @return cartId
	 */
	private Long ensureCartExistsInDB(Long userId, String username) {
		CartEntity cart = new CartEntity();
        cart.setUserId(userId);
        cart.setUsername(username);
        cart.setCreatedDate(LocalDateTime.now());
        CartEntity savedCart = cartRepository.save(cart);
        return savedCart.getCartId();
	}

	/**
	 * syncCartToDatabase
	 */
	public void syncCartToDatabase(Long userId) {
		log.info("syncCartToDatabase start()");
		CartDto cart = cacheService.getCart(userId, "");
		String redisKey = "cart::" + userId;
		if (cart != null && !cart.getItems().isEmpty()) {
			log.info("cart contains items");
			saveCartToDatabase(userId, cart);
			
		}
		redisTemplate.delete(redisKey);
		log.info("syncCartToDatabase exit()");
	}
	
	/**
	 * 
	 * @param userId
	 * @param cartDto
	 */
	private void saveCartToDatabase(Long userId, CartDto cartDto) {
		log.info("saveCartToDatabase start");
		CartEntity cart = cartRepository.findByUserId(userId).orElseGet(() -> {
			CartEntity newCart = new CartEntity();
			newCart.setUserId(userId);
			newCart.setUsername(cartDto.getUsername());
			newCart.setCreatedDate(LocalDateTime.now());
			return cartRepository.save(newCart);
		});

		cartItemRepository.deleteByCartId(cart.getCartId());
		cartItemRepository.flush();

		for (CartItemDto itemDTO : cartDto.getItems()) {
			CartItemEntity item = new CartItemEntity();
			item.setCartId(cart.getCartId());
			item.setProductId(itemDTO.getProductId());
			item.setProductName(itemDTO.getProductName());
			item.setQuantity(itemDTO.getQuantity());
			item.setUnitPrice(itemDTO.getUnitPrice());
			item.setTotalPrice(itemDTO.getTotalPrice());
			item.setAddedDate(LocalDateTime.now());
			cartItemRepository.save(item);
		}
		log.info("saveCartToDatabase exit");
	}

}    


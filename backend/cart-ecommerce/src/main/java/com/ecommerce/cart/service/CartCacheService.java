package com.ecommerce.cart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ecommerce.cart.mapper.CartMapper;
import com.ecommerce.cart.model.CartDto;
import com.ecommerce.cart.model.CartEntity;
import com.ecommerce.cart.model.CartItemDto;
import com.ecommerce.cart.model.CartItemEntity;
import com.ecommerce.cart.repository.CartItemRepo;
import com.ecommerce.cart.repository.CartRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author amoghavarshakm
 */
@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CartCacheService {
	
	private final CartRepo cartRepository;
	private final CartItemRepo cartItemRepository;
	private final CartMapper mapper;
	

	private static final String CACHE_NAME = "cart";
	
	@Cacheable(value = CACHE_NAME, key = "#userId", unless = "#result.items.empty")
	public CartDto getCart(Long userId, String username) {
	    log.info("getCart service entry for user:{}", username);
	    Optional<CartEntity> cartEntity = cartRepository.findByUserId(userId);
	    if (cartEntity.isPresent()) {
	        CartEntity cart = cartEntity.get();
	        return loadCartFromDatabase(cart);
	    } else {
	        CartDto emptyCart = new CartDto();
	        emptyCart.setUserId(userId);
	        emptyCart.setUsername(username);
	        emptyCart.setItems(new ArrayList<>());
	        return emptyCart;
	    }
	}
	
	private CartDto loadCartFromDatabase(CartEntity cart) {
		log.info("loadCartFromDatabase entry()");
		List<CartItemEntity> itemEntities = cartItemRepository.findByCartId(cart.getCartId());
		List<CartItemDto> itemDTOs = mapper.toDto(itemEntities);
		CartDto cartDto = new CartDto();
		cartDto.setCartId(cart.getCartId());
		cartDto.setUserId(cart.getUserId());
		cartDto.setUsername(cart.getUsername());
		cartDto.setItems(itemDTOs);
		log.info("loadCartFromDatabase exit()");
		return cartDto;

	}
	
	@CacheEvict(value = CACHE_NAME, key = "#userId")
	public void clearCart(Long userId) {
		log.info("clearCart method entry");
		cartRepository.findByUserId(userId).ifPresent(cart -> {
			cartItemRepository.deleteByCartId(cart.getCartId());
			cartRepository.delete(cart);
		});
		log.info("clearCart method exit");
	}
}

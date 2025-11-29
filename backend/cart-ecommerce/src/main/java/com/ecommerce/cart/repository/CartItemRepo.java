package com.ecommerce.cart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.cart.model.CartItemEntity;

/**
 * @author amoghavarshakm
 */
@Repository
public interface CartItemRepo extends JpaRepository<CartItemEntity, Long> {

	void deleteByCartIdAndProductId(Long cartId, Long productId);
	List<CartItemEntity> findByCartId(Long cartId);
	void deleteByCartId(Long cartId);
}

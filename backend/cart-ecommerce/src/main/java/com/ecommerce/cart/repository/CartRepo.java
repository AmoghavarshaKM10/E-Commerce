package com.ecommerce.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.cart.model.CartEntity;

/**
 * @author amoghavarshakm
 */
@Repository
public interface CartRepo extends JpaRepository<CartEntity, Long>{
	
	Optional<CartEntity> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}

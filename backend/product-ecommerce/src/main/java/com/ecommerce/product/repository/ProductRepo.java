package com.ecommerce.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.product.model.ProductEntity;

import jakarta.transaction.Transactional;

/**
 * @author amoghavarshakm
 */
@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Integer> {

	@Modifying
	@Transactional
	@Query("UPDATE ProductEntity p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.id = :id")	
	void reduceStockQuantity(@Param("id") Long id,@Param("quantity") Integer stockQuantity);
	
	@Modifying
	@Transactional
	@Query("UPDATE ProductEntity p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id = :id")	
	void updateStockQuantity(@Param("id") Long id,@Param("quantity") Integer stockQuantity);
	
	@Query("SELECT p.productId, p.stockQuantity, p.price FROM ProductEntity p WHERE p.productId IN :productIds")
	List<Object[]> findProductStockByIds(@Param("productIds") List<Long> productIds);
}

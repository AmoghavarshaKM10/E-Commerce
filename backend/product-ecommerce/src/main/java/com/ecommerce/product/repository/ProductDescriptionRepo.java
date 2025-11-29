package com.ecommerce.product.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.product.model.ProductDescriptionEntity;

import jakarta.transaction.Transactional;

/**
 * @author amoghavarshakm
 */
@Repository
public interface ProductDescriptionRepo extends JpaRepository<ProductDescriptionEntity, Long> {
	
	@Modifying
    @Transactional
    @Query("UPDATE ProductDescriptionEntity pd SET pd.ratings = :newRating, pd.reviewsCount = pd.reviewsCount + 1 WHERE pd.productId = :productId")
    void updateProductRating(@Param("productId") Long productId, @Param("newRating") BigDecimal newRating);
}

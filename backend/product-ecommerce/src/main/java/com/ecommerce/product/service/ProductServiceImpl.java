package com.ecommerce.product.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecommerce.parent.config.AuditService;
import com.ecommerce.product.mapper.ProductMapper;
import com.ecommerce.product.model.ProductDescriptionDto;
import com.ecommerce.product.model.ProductDescriptionEntity;
import com.ecommerce.product.model.ProductDto;
import com.ecommerce.product.model.ProductEntity;
import com.ecommerce.product.repository.ProductDescriptionRepo;
import com.ecommerce.product.repository.ProductRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author amoghavarshakm
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
	
	private final ProductRepo productRepo;
	private final ProductDescriptionRepo descriptionRepo;
	private final ProductMapper mapper;
	private final AuditService auditService;
	
	@Override
	public List<ProductDto> getAllProducts() {
		log.info("getAllProducts service method entry");
		List<ProductEntity> entity = productRepo.findAll();
		log.info("getAllProducts service method exit");
		return mapper.toDto(entity);
	}

	@Override
	public ProductDescriptionDto getProductDescription(Long productId) {
		log.info("getProductDescription service method entry for ProductId:{}",productId);
		ProductDescriptionEntity entity = descriptionRepo.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product Not Found For Id: " + productId));
		log.info("getProductDescription service method exit for ProductId:{}",productId);
		return mapper.toDescriptionDto(entity);
	}

	@Override
	public void reduceStock(Long id, Integer quantity) {
		log.info("reverseStock service method entry");
		productRepo.reduceStockQuantity(id, quantity);
		log.info("reverseStock service method exit");
	}

	@Override
	public void updateStock(Long id, Integer quantity) {
		log.info("updateStock service method entry");
		productRepo.updateStockQuantity(id, quantity);
		log.info("updateStock service method exit");
	}

	@Override
	public void updateProductRating(Long productId, BigDecimal newUserRating) {
        log.info("updateProductRating method entry for ProductId:{}",productId);
        ProductDescriptionEntity description = descriptionRepo.findById(productId)
        		.orElseThrow(() -> new RuntimeException("Product Details Not Found For Id:" + productId));
        BigDecimal newAverageRating = calculateNewRating(description.getRatings(), description.getReviewsCount(), newUserRating);
        descriptionRepo.updateProductRating(productId, newAverageRating);
        log.info("updateProductRating method exit for ProductId:{}",productId);
    }

    private static BigDecimal calculateNewRating(BigDecimal currentRating, Integer currentReviewsCount, BigDecimal newRating) {
        if (currentReviewsCount == 0) {
            return newRating; 
        }
        BigDecimal totalCurrentRating = currentRating.multiply(BigDecimal.valueOf(currentReviewsCount));
        BigDecimal newTotalRating = totalCurrentRating.add(newRating);
        BigDecimal newReviewsCount = BigDecimal.valueOf(currentReviewsCount + 1);
        return newTotalRating.divide(newReviewsCount, 2, RoundingMode.HALF_UP);
    }

	@Override
	public List<ProductDto> getStockDetails(List<Long> productIds) {
		log.info("getStockDetails service method entry");
		List<Object[]> stockDetails = productRepo.findProductStockByIds(productIds);
		List<ProductDto> productDTOs = new ArrayList<>();
		if (stockDetails != null && !stockDetails.isEmpty()) {
			productDTOs = stockDetails.stream().map(result -> {
				ProductDto dto = new ProductDto();
				dto.setProductId((Long) result[0]);
				dto.setStockQuantity((Integer) result[1]);
				dto.setPrice((BigDecimal) result[2]);
				return dto;
			}).collect(Collectors.toList());
		}
		log.info("getStockDetails service method exit");

		return productDTOs;
	}
	
	

	
	
}

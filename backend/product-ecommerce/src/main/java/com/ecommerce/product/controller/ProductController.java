package com.ecommerce.product.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.parent.config.AuditService;
import com.ecommerce.product.model.ProductDescriptionDto;
import com.ecommerce.product.model.ProductDto;
import com.ecommerce.product.model.ProductIdRequestDto;
import com.ecommerce.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author amoghavarshakm
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {

	private final ProductService service;
	private final AuditService auditService;

	/**
	 * Get all products
	 */
	@GetMapping("/all")
	public ResponseEntity<List<ProductDto>> getAllProducts() {
		log.info("getAllProducts api entry");
		return ResponseEntity.ok(service.getAllProducts());
	}

	/**
	 * Get product description by ID
	 */
	@GetMapping("/{productId}/description")
	public ResponseEntity<ProductDescriptionDto> getProductDescriptionById(@PathVariable Long productId) {
		log.info("getProductDescriptionById api entry");
		return ResponseEntity.ok(service.getProductDescription(productId));
	}

	/**
	 * Update product rating
	 */
	@PatchMapping("/{productId}/rating")
	public ResponseEntity<String> updateProductRating(@PathVariable Long productId, @RequestParam BigDecimal rating) {
		log.info("updateProductRating api entry");
		service.updateProductRating(productId, rating);
		return ResponseEntity.ok("Rating update Success");
	}

	/**
	 * Adds quantity to product stock
	 * 
	 * @param productId the ID of the product to update
	 * @param quantity  the amount to add to stock
	 * @return success message
	 */
	@PatchMapping("/{productId}/stock/add")
	public ResponseEntity<String> addStock(@PathVariable Long productId, @RequestParam Integer quantity) {
		log.info("addStock api entry");
		service.updateStock(productId, quantity);
		return ResponseEntity.ok("Stock added successfully");
	}

	/**
	 * Reduces quantity from product stock
	 * 
	 * @param productId the ID of the product to update
	 * @param quantity  the amount to reduce from stock
	 * @return success message
	 */
	@PatchMapping("/{productId}/stock/reduce")
	public ResponseEntity<String> reduceStock(@PathVariable Long productId, @RequestParam Integer quantity) {
		log.info("reduceStock api entry");
		service.reduceStock(productId, quantity);
		return ResponseEntity.ok("Stock reduced successfully");
	}
	
	/**
	 * 
	 * @param request
	 * @return stockDetails
	 */
	@PostMapping("/stock")
	public ResponseEntity<List<ProductDto>> getStock(@RequestBody ProductIdRequestDto request){
		log.info("getStock api entry");
		return ResponseEntity.ok(service.getStockDetails(request.getProductIds()));
	}

}

package com.ecommerce.product.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;



@Entity
@Table(name = "product_ecommerce",schema = "public")
@Data
public class ProductEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="product_id")
	private Long productId;
	
	@Column(name="name")
	private String name;
    
	@Column(name="price")
    private BigDecimal price;
    
	@Column(name = "stock_quantity")
    private Integer stockQuantity;
    
	@Column(name = "category")
    private String category;
	
	@Column(name = "product_image_url")
	private String imageUrl;
}

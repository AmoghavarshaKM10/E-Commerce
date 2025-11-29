package com.ecommerce.product.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author amoghavarshakm
 */
@Entity
@Table(name = "product_description", schema = "public")
@Data
public class ProductDescriptionEntity {

	@Id
    @Column(name = "product_id")
    private Long productId;  

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "ratings")
    private BigDecimal ratings;
    
    @Column(name = "reviews_count")
    private int reviewsCount;

    @Column(name = "return_period")
    private Integer returnPeriod ;

    @Column(name = "warranty_period")
    private Integer warrantyPeriod;
}

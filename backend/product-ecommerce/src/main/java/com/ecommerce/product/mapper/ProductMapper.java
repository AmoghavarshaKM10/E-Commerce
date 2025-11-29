package com.ecommerce.product.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import org.mapstruct.ReportingPolicy;

import com.ecommerce.product.model.ProductDescriptionDto;
import com.ecommerce.product.model.ProductDescriptionEntity;
import com.ecommerce.product.model.ProductDto;
import com.ecommerce.product.model.ProductEntity;

/**
 * @author amoghavarshakm
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

	ProductDto toDto(ProductEntity entity);
    ProductEntity toEntity(ProductDto dto); 
    
    ProductDescriptionEntity toDescriptionEntity(ProductDescriptionDto dto);
    ProductDescriptionDto toDescriptionDto(ProductDescriptionEntity entity);
    
    List<ProductDto> toDto(List<ProductEntity> entities);
    List<ProductEntity> toEntity(List<ProductDto> dtos);
}


package com.ecommerce.cart.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.ecommerce.cart.model.CartItemDto;
import com.ecommerce.cart.model.CartItemEntity;

/**
 * @author amoghavarshakm
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartMapper {

	List<CartItemDto> toDto(List<CartItemEntity> entity);
}

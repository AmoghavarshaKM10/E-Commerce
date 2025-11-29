package com.ecommerce.order.config;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ecommerce.order.model.ProductDto;
import com.ecommerce.order.model.ProductIdRequestDto;

@FeignClient(name = "product-ecommerce", url = "${service.product.url}", configuration = FeignConfig.class)
public interface ProductServiceClient {

	@PostMapping("/stock")
	List<ProductDto> getProductsStock(@RequestBody ProductIdRequestDto request);
}

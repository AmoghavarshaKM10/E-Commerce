package com.ecommerce.order.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "cart-ecommerce", url = "${service.cart.url}", configuration = FeignConfig.class)
public interface CartServiceClient {
    
    @DeleteMapping("/clear")
    void clearCart(@RequestHeader("X-User-Id") Long userId);
}

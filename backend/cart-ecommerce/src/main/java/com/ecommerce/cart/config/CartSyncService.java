package com.ecommerce.cart.config;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ecommerce.cart.service.CartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartSyncService {

	private final CartService cartService;
	private final RedisTemplate<String, Object> redisTemplate;

	/**
	 * A Scheduler to Sync Cart to DB from Redis
	 */
	@Scheduled(fixedRate = 9 * 60 * 1000)
	public void syncCartsBeforeTtl() {
		log.info("Starting scheduled cart sync before TTL expiration");
		try {
			Set<String> cartKeys = redisTemplate.keys("cart::*");
			if (cartKeys != null && !cartKeys.isEmpty()) {
				log.info("Found {} active carts to sync", cartKeys.size());
				for (String cartKey : cartKeys) {
					try {
						Long userId = extractUserIdFromKey(cartKey);
						if (userId != null) {
							cartService.syncCartToDatabase(userId);
							log.debug("Successfully synced cart for user: {}", userId);
						}
					} catch (Exception e) {
						log.error("Failed to sync cart for key: {}", cartKey, e.getMessage());
						throw e;
					}
				}
			} else {
				log.info("No active carts found for sync");
			}

		} catch (Exception e) {
			log.error("Error during scheduled cart sync", e.getMessage());
			throw e;
		}
	}

	/**
	 * 
	 * @param cartKey
	 * @return userId from cartKey
	 */
	private static Long extractUserIdFromKey(String cartKey) {
		try {
			String userIdStr = cartKey.substring(6);
			return Long.parseLong(userIdStr);
		} catch (Exception e) {
			log.error("Invalid cart key format: {}", cartKey);
			return null;
		}
	}

}

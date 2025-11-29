package com.ecommerce.cart.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

/**
 * @author amoghavarshakm
 */
@Configuration
@EnableCaching
public class CartRedisConfig {
	
	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Value("${spring.data.redis.password}")
	private String redisPassword;

	@Value("${spring.data.redis.database}")
	private int redisDatabase;

	@Value("${spring.data.redis.timeout}")
	private int redisTimeout;

	@Value("${spring.data.redis.jedis.pool.max-active}")
	private int maxActive;

	@Value("${spring.data.redis.jedis.pool.max-idle}")
	private int maxIdle;

	@Value("${spring.data.redis.jedis.pool.min-idle}")
	private int minIdle;

	@Value("${spring.data.redis.jedis.pool.max-wait}")
	private long maxWait;

	/**
	 * 
	 * @return redisConnection
	 */
	@Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setDatabase(redisDatabase);
        
        if (!redisPassword.isEmpty()) {
            config.setPassword(RedisPassword.of(redisPassword));
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWait(Duration.ofMillis(maxWait));
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .connectTimeout(Duration.ofMillis(redisTimeout))
                .readTimeout(Duration.ofMillis(redisTimeout))
                .usePooling()
                .poolConfig(poolConfig)
                .build();

        return new JedisConnectionFactory(config, clientConfig);
    }
	/**
	 * 
	 * @param connectionFactory
	 * @return redisTemplate
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}

	/**
	 * 
	 * @param connectionFactory
	 * @return cacheManager
	 */
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(10)).disableCachingNullValues()
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

		return RedisCacheManager.builder(connectionFactory).cacheDefaults(cacheConfig).build();
	}
}

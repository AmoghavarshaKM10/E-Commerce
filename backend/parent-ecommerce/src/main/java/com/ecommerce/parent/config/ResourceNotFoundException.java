package com.ecommerce.parent.config;

/**
 * @author amoghavarshakm
 */
public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) {
		super(message);
	}
}

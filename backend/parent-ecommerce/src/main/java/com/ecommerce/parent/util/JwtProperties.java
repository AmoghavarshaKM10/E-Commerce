package com.ecommerce.parent.util;

/**
 * @author amoghavarshakm
 */
public class JwtProperties {
	public static final long ACCESS_TOKEN_EXP = 1000 * 60 * 59; 
	public static final long REFRESH_TOKEN_EXP = 1000L * 60 * 60 * 24 * 7; 

	public static final String ACCESS_SECRET = "SuperSecretKeyForAccessTokenAtLeast256BitsLong!123";
	public static final String REFRESH_SECRET = "SuperSecretKeyForRefreshTokenAtLeast256BitsLong!456";
}


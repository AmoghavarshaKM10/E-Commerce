package com.ecommerce.parent.util;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
/**
 * @author amoghavarshakm
 */
@Component
public class JwtUtil {

	private final Key accessKey = Keys.hmacShaKeyFor(JwtProperties.ACCESS_SECRET.getBytes());
	private final Key refreshKey = Keys.hmacShaKeyFor(JwtProperties.REFRESH_SECRET.getBytes());

	public String generateAccessToken(Long userId, String email) {
		return Jwts.builder().setSubject(email).claim("userId", userId).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXP))
				.signWith(accessKey).compact();
	}

	public String generateRefreshToken(Long userId, String email) {
		return Jwts.builder().setSubject(email).claim("userId", userId).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + JwtProperties.REFRESH_TOKEN_EXP))
				.signWith(refreshKey).compact();
	}

	public boolean validateAccessToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public boolean validateRefreshToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public String getEmailFromToken(String token, boolean isRefresh) {
		Claims claims = Jwts.parserBuilder().setSigningKey(isRefresh ? refreshKey : accessKey).build()
				.parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public Long getUserIdFromToken(String token, boolean isRefresh) {
		Claims claims = Jwts.parserBuilder().setSigningKey(isRefresh ? refreshKey : accessKey).build()
				.parseClaimsJws(token).getBody();
		return claims.get("userId", Long.class);
	}
}

package com.ecommerce.user.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ecommerce.parent.model.UserDetailsDto;
import com.ecommerce.parent.util.JwtUtil;
import com.ecommerce.user.service.UserLookUpService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author amoghavarshakm
 */
@Component
public class JwtRefreshHandler {

	private final JwtUtil jwtUtil;
	private final UserLookUpService lookUpService;

	public JwtRefreshHandler(JwtUtil jwtUtil, UserLookUpService lookUpService) {
		this.jwtUtil = jwtUtil;
		this.lookUpService = lookUpService;
	}

	public String refreshTokenFromCookie(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Optional<Cookie> refreshCookie = Optional.ofNullable(request.getCookies()).flatMap(cookies -> {
			for (Cookie c : cookies) {
				if ("refreshToken".equals(c.getName()))
					return Optional.of(c);
			}
			return Optional.empty();
		});

		if (refreshCookie.isEmpty())
			return null;

		String refreshToken = refreshCookie.get().getValue();
		if (!jwtUtil.validateRefreshToken(refreshToken))
			return null;

		Long userId = jwtUtil.getUserIdFromToken(refreshToken, true);
		UserDetailsDto detailsDto = lookUpService.findByUserId(userId);
		if (detailsDto == null)
			return null;

		String newAccessToken = jwtUtil.generateAccessToken(detailsDto.getUserId(), detailsDto.getEmail());

		return newAccessToken;
	}
}

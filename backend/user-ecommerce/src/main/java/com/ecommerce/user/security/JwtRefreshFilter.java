package com.ecommerce.user.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author amoghavarshakm
 */
@Component
public class JwtRefreshFilter extends OncePerRequestFilter {

	private final JwtRefreshHandler refreshHandler;

	public JwtRefreshFilter(JwtRefreshHandler refreshHandler) {
		this.refreshHandler = refreshHandler;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.equals("/users/login") || uri.equals("/users/signUp");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String newToken = refreshHandler.refreshTokenFromCookie(request, response);
		if (newToken == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Authorization", "Bearer " + newToken);
		response.setContentType("application/json");
		response.getWriter().write("{\"token\":\"" + newToken + "\"}");
		response.getWriter().flush();

	}
}

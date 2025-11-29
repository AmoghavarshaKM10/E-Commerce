package com.ecommerce.parent.util;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	public JwtFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.equals("/users/login") || uri.equals("/users/signUp") || uri.equals("/users/token/refresh");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String accessToken = authHeader.substring(7);
			if (jwtUtil.validateAccessToken(accessToken)) {
				String email = jwtUtil.getEmailFromToken(accessToken, false);
				Long userId = jwtUtil.getUserIdFromToken(accessToken, false);

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null,
						List.of(new SimpleGrantedAuthority("ROLE_USER")));
				SecurityContextHolder.getContext().setAuthentication(auth);

				request.setAttribute("X-User-Id", userId);
	            request.setAttribute("X-Username", email);
				
	            
				filterChain.doFilter(request, response);
				return;
			}
		}
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}

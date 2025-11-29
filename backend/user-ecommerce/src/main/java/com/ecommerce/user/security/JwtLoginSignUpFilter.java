package com.ecommerce.user.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.stereotype.Component;

import com.ecommerce.parent.model.LoginRequest;
import com.ecommerce.parent.model.SignupRequest;
import com.ecommerce.parent.model.UserDetailsDto;
import com.ecommerce.parent.util.JwtProperties;
import com.ecommerce.parent.util.JwtUtil;
import com.ecommerce.user.service.UserLookUpService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author amoghavarshakm
 */
@Component
public class JwtLoginSignUpFilter extends AbstractAuthenticationProcessingFilter {

	private final JwtUtil jwtUtil;
	private final UserLookUpService service;
	private final List<String> urls = List.of("/users/login", "/users/signUp");
	private final PasswordEncoder passwordEncoder;

	public JwtLoginSignUpFilter(JwtUtil jwtUtil, UserLookUpService service, PasswordEncoder passwordEncoder) {
		super("/dummy");
		this.jwtUtil = jwtUtil;
		this.service = service;
		this.passwordEncoder = passwordEncoder;
		setAuthenticationManager(auth -> auth); 
	}

	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		return urls.contains(request.getRequestURI());
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String body = new String(request.getInputStream().readAllBytes());
      
        if (body == null || body.trim().isEmpty()) {
            throw new BadCredentialsException("Empty request body");
        }
        
		String path = request.getRequestURI();
		if (path.equals("/users/login")) {
			LoginRequest login = new ObjectMapper().readValue(body, LoginRequest.class);
			var user = service.findByEmail(login.email());

			if (user == null) {
				throw new BadCredentialsException("User not found");
			}
			boolean isPasswordCorrect = passwordEncoder.matches(login.password(), user.getHashPassword());

			if (!isPasswordCorrect) {
				throw new BadCredentialsException("Invalid password");
			}

			return new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of());
		} else { 
			SignupRequest signup = new ObjectMapper().readValue(body, SignupRequest.class);
			service.signup(signup);
			return new UsernamePasswordAuthenticationToken(signup.email(), null, List.of());
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			org.springframework.security.core.Authentication authResult) throws IOException {

		String email = authResult.getName();
		UserDetailsDto dto = service.findByEmail(email);

		String accessToken = jwtUtil.generateAccessToken(dto.getUserId(), email);
		String refreshToken = jwtUtil.generateRefreshToken(dto.getUserId(), email);

		response.setContentType("application/json");
		response.getWriter().write("{\"token\":\"" + accessToken + "\"}");

		Cookie cookie = new Cookie("refreshToken", refreshToken);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge((int) (JwtProperties.REFRESH_TOKEN_EXP / 1000));
		response.addCookie(cookie);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write("{\"error\":\"" + failed.getMessage() + "\"}");
	}

}

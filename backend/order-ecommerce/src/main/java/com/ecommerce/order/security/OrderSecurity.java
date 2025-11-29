package com.ecommerce.order.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ecommerce.parent.util.JwtFilter;

@Configuration
@EnableWebSecurity
public class OrderSecurity {

	private final JwtFilter authFilter;

	public OrderSecurity(JwtFilter authFilter) {
		this.authFilter = authFilter;

	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
		.cors(Customizer.withDefaults()).authorizeHttpRequests(
				auth -> auth.requestMatchers("/users/login", "/users/signUp", "/users/token/refresh").permitAll()
						.anyRequest().authenticated());
		http.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}

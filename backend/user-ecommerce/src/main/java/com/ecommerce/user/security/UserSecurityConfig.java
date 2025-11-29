package com.ecommerce.user.security;
import com.ecommerce.user.config.CorsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.ecommerce.parent.util.JwtFilter;

/**
 * @author amoghavarshakm
 */
@Configuration
@EnableWebSecurity
public class UserSecurityConfig {

    private final WebMvcConfigurer corsConfigurer;

    private final CorsConfig corsConfig;

	private final JwtLoginSignUpFilter loginSignUpFilter;
	private final JwtRefreshFilter refreshFilter;
	private final JwtFilter authFilter;

	public UserSecurityConfig(JwtLoginSignUpFilter loginSignUpFilter, JwtRefreshFilter refreshFilter,
			JwtFilter authFilter, CorsConfig corsConfig, WebMvcConfigurer corsConfigurer) {
		this.loginSignUpFilter = loginSignUpFilter;
		this.refreshFilter = refreshFilter;
		this.authFilter = authFilter;
		this.corsConfig = corsConfig;
		this.corsConfigurer = corsConfigurer;

	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
		 .cors(Customizer.withDefaults()).authorizeHttpRequests(
				auth -> auth.requestMatchers("/users/login", "/users/signUp", "/users/token/refresh").permitAll()
						.anyRequest().authenticated())
		.addFilterBefore(loginSignUpFilter, UsernamePasswordAuthenticationFilter.class)
		.addFilterBefore(refreshFilter, UsernamePasswordAuthenticationFilter.class)
		.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}

//package com.ecommerce.parent.security;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import com.ecommerce.parent.util.JwtFilter;
//
//@Configuration
//@EnableWebSecurity
//
//public class SecurityConfig {
//
//  
//
//
//    private final JwtFilter jwtFilter;
//
//    
//	 public SecurityConfig(
//             JwtFilter jwtFilter) {
//		 	
//		 	this.jwtFilter = jwtFilter;
//		 
//		 	
//		 	
//		 
//}
//    
//	 @Bean
//	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//	        http
//	            .csrf(csrf -> csrf.disable())
//	            .authorizeHttpRequests(auth -> auth
//	                .requestMatchers("/users/login", "/users/signUp", "/users/token/refresh").permitAll()
//	                .anyRequest().authenticated())
//	            .httpBasic(httpBasic -> httpBasic.disable())
//	            .formLogin(form -> form.disable())
//	            .sessionManagement(session -> session.disable());
//	        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//	    
//	   
//	        return http.build();
//	
//	 }
//	 
//	 @Bean
//	 public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//	     return config.getAuthenticationManager();
//	 }
//}

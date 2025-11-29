package com.ecommerce.user.controller;

import java.net.URI;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.function.Consumer;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.user.model.LoginRequest;
import com.ecommerce.user.model.SettingsChangeRequest;
import com.ecommerce.user.model.SignupRequest;
import com.ecommerce.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService service;

	@PostMapping("/signUp")
	public void signup(@RequestBody SignupRequest req) {
		
	}

	@PostMapping("/login")
	public void login(@RequestBody LoginRequest request) {
		
	}

	@GetMapping("/token/refresh")
	public void getRefreshToken() {
	}
	
	@PostMapping("/settings")
	public ResponseEntity<?> updateSettings(@RequestBody SettingsChangeRequest request,@RequestHeader("X-User-Id") Long userId){
		return  ResponseEntity.ok(service.updateSettings(userId, request));
		}
	}


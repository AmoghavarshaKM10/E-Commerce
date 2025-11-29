package com.ecommerce.parent.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetailsDto {

	private String email;
	private Long userId;
	private String hashPassword;
}

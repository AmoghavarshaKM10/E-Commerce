package com.ecommerce.user.service;

import com.ecommerce.parent.model.SignupRequest;
import com.ecommerce.parent.model.UserDetailsDto;


public interface UserLookUpService {

	/**
	 * 
	 * @param email
	 * @return userDetails
	 */
	 UserDetailsDto findByEmail(String email);
	 
	 /**
	  * 
	  * @param userId
	  * @return userDetails
	  */
	 UserDetailsDto findByUserId(Long userId);
	 
	 /**
	  * 
	  * @param req
	  */
	 void signup(SignupRequest req);
	 
}

package com.ecommerce.user.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.parent.config.UserAlreadyExistsException;
import com.ecommerce.parent.model.SignupRequest;
import com.ecommerce.parent.model.UserDetailsDto;
import com.ecommerce.user.model.Users;
import com.ecommerce.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserLookUpServiceImpl implements UserLookUpService {

	private final UserRepository userRepository ;
    private final PasswordEncoder passwordEncoder;
	
	@Override
	public UserDetailsDto findByEmail(String email) {
		 var user = userRepository.findByEmail(email)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	        return new UserDetailsDto(user.getEmail(), user.getUserId(), user.getHashPassword());
	}

	@Override
	public UserDetailsDto findByUserId(Long userId) {
		 var user = userRepository.findById(userId)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	        return new UserDetailsDto(user.getEmail(), user.getUserId(),null);
	}

	@Override
	public void signup(SignupRequest req) {
		Optional<Users> checkIfPresent = userRepository.findByEmail(req.email());
    	if(checkIfPresent.isPresent()) {
    		throw new UserAlreadyExistsException("User already exist.Please Login");
    	}
        String hash = passwordEncoder.encode(req.password());
        Users user = new Users();
        user.setEmail(req.email());
        user.setFullName(req.name());
        user.setHashPassword(hash);
        userRepository.save(user);
    }
		
	}



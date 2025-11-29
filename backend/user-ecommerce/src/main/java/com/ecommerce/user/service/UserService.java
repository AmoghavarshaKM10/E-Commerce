package com.ecommerce.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.parent.config.ResourceNotFoundException;
import com.ecommerce.user.model.SettingsChangeRequest;
import com.ecommerce.user.model.Users;
import com.ecommerce.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author amoghavarshakm
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public String updateSettings(Long userId, SettingsChangeRequest req) {

        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        switch (req.changeType().toString().toLowerCase()) {

            case "email" -> {
                if (req.newEmail() == null || req.newEmail().isBlank())
                    throw new RuntimeException("New email required");
                user.setEmail(req.newEmail());
            }

            case "address" -> {
                if (req.newAddress() == null || req.newAddress().isBlank())
                    throw new RuntimeException("New address required");
                user.setAddress(req.newAddress());
            }

            case "password" -> {
                if (req.currentPassword() == null || req.newPassword() == null)
                    throw new RuntimeException("Current and new passwords required");

                boolean passwordMatch = passwordEncoder.matches(
                        req.currentPassword(), user.getHashPassword()
                );

                if (!passwordMatch)
                	throw new RuntimeException("Invalid current password");

                user.setHashPassword(passwordEncoder.encode(req.newPassword()));
            }

            default -> throw new RuntimeException("Invalid changeType");
        }

        userRepo.save(user);
        return "Updated successfully";
    }
}

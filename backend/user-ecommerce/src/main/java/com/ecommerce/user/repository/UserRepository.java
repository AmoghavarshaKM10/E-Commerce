package com.ecommerce.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.user.model.Users;

/**
 * @author amoghavarshakm
 */
@Repository
public interface UserRepository extends JpaRepository<Users, Long> { 

	Optional<Users> findByEmail(String email);
}
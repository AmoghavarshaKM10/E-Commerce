package com.ecommerce.user.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * @author amoghavarshakm
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id") 
    private Long userId;

	@Column(name = "email")
    private String email;
	
	@Column(name = "hash_password")
    private String hashPassword;
	
	@Column(name = "full_name")
    private String fullName;
	
	@Column(name = "address")
    private String address;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}
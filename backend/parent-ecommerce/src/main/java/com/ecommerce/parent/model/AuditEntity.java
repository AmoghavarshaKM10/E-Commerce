package com.ecommerce.parent.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * @author amoghavarshakm
 */
@Document(collection = "audit_logs")
@Data
public class AuditEntity {

	@Id
    private String id;
    private String serviceName;
    private String action;
    private String username;
    private LocalDateTime timestamp =LocalDateTime.now();
    
}

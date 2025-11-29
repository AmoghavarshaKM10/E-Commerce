package com.ecommerce.parent.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.ecommerce.parent.model.AuditEntity;

/**
 * @author amoghavarshakm
 */
@Component
public class AuditService {
	
    @Autowired
    private MongoTemplate mongoTemplate;
    
    public void logAction(String serviceName, String action, String username) {
        AuditEntity audit = new AuditEntity();
        audit.setServiceName(serviceName);
        audit.setAction(action);
        audit.setUsername(username);  
        mongoTemplate.save(audit);
    }
}
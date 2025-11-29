package com.ecommerce.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.order.model.OrderItemEntity;

/**
 * @author amoghavarshakm
 */
@Repository
public interface OrderItemRepo extends JpaRepository<OrderItemEntity, Long> {
	
	List<OrderItemEntity> findByOrderId(Long orderId);
    List<OrderItemEntity> findByOrderIdIn(List<Long> orderIds); 
    void deleteByOrderId(Long orderId);
}

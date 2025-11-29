package com.ecommerce.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.order.model.OrderEntity;
import com.ecommerce.order.model.OrderStatus;

/**
 * @author amoghavarshakm
 */
@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,Long>{

	List<OrderEntity> findByUserIdOrderByCreatedDateDesc(Long userId);
    Optional<OrderEntity> findByPaymentId(String paymentId);
    Optional<OrderEntity> findByOrderIdAndUserId(Long orderId, Long userId);
}

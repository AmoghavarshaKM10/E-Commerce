package com.ecommerce.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.order.config.CartServiceClient;
import com.ecommerce.order.config.PayPalPaymentService;
import com.ecommerce.order.config.ProductServiceClient;
import com.ecommerce.order.model.CartItemDto;
import com.ecommerce.order.model.CheckOutRequest;
import com.ecommerce.order.model.OrderDto;
import com.ecommerce.order.model.OrderEntity;
import com.ecommerce.order.model.OrderItemDto;
import com.ecommerce.order.model.OrderItemEntity;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.model.PaymentRequest;
import com.ecommerce.order.model.PaymentResponse;
import com.ecommerce.order.model.ProductDto;
import com.ecommerce.order.model.ProductIdRequestDto;
import com.ecommerce.order.repository.OrderItemRepo;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.parent.config.PaymentException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author amoghavarshakm
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl {

	private final OrderRepository orderRepository;
	private final OrderItemRepo orderItemRepository;
	private final CartServiceClient cartService;
	private final ProductServiceClient productService;
	private final PayPalPaymentService payPalService;

	/**
	 * 
	 * @param userId
	 * @param username
	 * @param request
	 * @return new Order
	 */
	public OrderDto checkout(Long userId, String username, CheckOutRequest request) {
		log.info("Processing checkout for user: {}", userId);
		List<CartItemDto> cartItems = request.getCartItems();
		OrderDto newOrder = null;
		try {
			stockCheck(cartItems);
			newOrder = createOrder(userId, username, request, cartItems);

			PaymentRequest paymentRequest = new PaymentRequest();
			paymentRequest.setOrderId(newOrder.getOrderId());
			paymentRequest.setUserId(userId);
			paymentRequest.setAmount(newOrder.getTotalAmount());
			paymentRequest.setMethod(request.getPaymentMethod());
			paymentRequest.setEmail(username);
			paymentRequest.setContact(request.getContact());

			PaymentResponse paymentResponse = payPalService.processPayment(paymentRequest);

			if (paymentResponse.getApprovalUrl() != null) {
				updateOrderStatus(newOrder.getOrderId(), OrderStatus.PENDING.toString(), paymentResponse.getPaymentId(),
						"PENDING");
				newOrder.setPaymentApprovedUrl(paymentResponse.getApprovalUrl());
				newOrder.setPaymentStatus("PENDING");

				log.info("Order created pending approval: {}. Approval URL: {}", newOrder.getOrderId(),
						paymentResponse.getApprovalUrl());
				clearCart(userId);
				return newOrder;
			} else {
				throw new PaymentException("Payment processing failed: " + paymentResponse.getMessage(),
						"PAYMENT_FAILED");
			}
		} catch (Exception e) {
			if (newOrder != null) {
				updateOrderStatus(newOrder.getOrderId(), OrderStatus.FAILED.toString(), null, null);
			}
			log.error("Checkout failed for user {}: {}", userId, e.getMessage());
			if (e instanceof PaymentException) {
				throw e;
			} else {
				throw new PaymentException("Checkout failed: " + e.getMessage(), "CHECKOUT_ERROR");
			}
		}

	}

	/**
	 * 
	 * @param orderId
	 * @param userId
	 * @return order
	 */
	public OrderDto getOrder(Long orderId, Long userId) {
		log.info("Fetching order: {} for user: {}", orderId, userId);
		OrderEntity order = orderRepository.findByOrderIdAndUserId(orderId, userId)
				.orElseThrow(() -> new RuntimeException("Order not found"));
		List<OrderItemEntity> orderItems = orderItemRepository.findByOrderId(orderId);
		log.info("Fetching order: {} for user: {} complete.", orderId, userId);
		return convertToFullDTO(order, orderItems);
	}

	/**
	 * 
	 * @param userId
	 * @return orders
	 */
	public List<OrderDto> getUserOrdersWithItems(Long userId) {
		log.info("Fetching orders with items for user: {}", userId);
		List<OrderEntity> orders = orderRepository.findByUserIdOrderByCreatedDateDesc(userId);
		if (orders.isEmpty()) {
			return new ArrayList<>();
		}
		List<Long> orderIds = orders.stream().map(OrderEntity::getOrderId).collect(Collectors.toList());
		List<OrderItemEntity> allItems = orderItemRepository.findByOrderIdIn(orderIds);
		Map<Long, List<OrderItemEntity>> itemsByOrderId = allItems.stream()
				.collect(Collectors.groupingBy(OrderItemEntity::getOrderId));
		log.info("Fetching orders with items for user: {} exit.", userId);
		return orders.stream().map(
				order -> convertToFullDTO(order, itemsByOrderId.getOrDefault(order.getOrderId(), new ArrayList<>())))
				.collect(Collectors.toList());
	}

	/**
	 * 
	 * @param orderId
	 * @param userId
	 * @return updatedOrder with status
	 */
	public OrderDto cancelOrder(Long orderId, Long userId) {
		log.info("Cancelling order: {} for user: {}", orderId, userId);
		OrderEntity order = orderRepository.findByOrderIdAndUserId(orderId, userId)
				.orElseThrow(() -> new RuntimeException("Order not found"));
		OrderStatus currentStatus = OrderStatus.valueOf(order.getStatus());
		if (currentStatus == OrderStatus.PENDING && currentStatus == OrderStatus.CANCELLED) {
			throw new RuntimeException("Order cannot be cancelled in current status");
		}
		order.setStatus(OrderStatus.CANCELLED.toString());
		orderRepository.save(order);
		return convertToBasicDTO(order);
	}
	
	/**
	 * 
	 * @param orderId
	 * @param status
	 * @return order after updating new status.
	 */
	public OrderDto updateOrderStatus(Long orderId, String orderStatus, String paymentId, String paymentStatus) {
		log.info("Updating orderId: {}", orderId);
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found"));
		
		order.setStatus(orderStatus);
		if (paymentStatus != null) {
			order.setPaymentStatus(paymentStatus);
		}
		if (paymentId != null) {
			order.setPaymentId(paymentId);
		}
		orderRepository.save(order);
		log.info("Order status update complete for orderId:{}", orderId);
		return convertToBasicDTO(order);
	}
	
	public void updateOrderStatusByPaymentId (String paymentId, String orderStatus, String paymentStatus) {
		log.info("Updating order & payment status by paymentId:{}",paymentId);
		OrderEntity order = orderRepository.findByPaymentId(paymentId)
				.orElseThrow(() -> new RuntimeException("Order not found for PaymentId"));
		order.setStatus(orderStatus);
		order.setPaymentStatus(paymentStatus);
		orderRepository.save(order);
		log.info("Updating order & payment status by paymentId:{} complete",paymentId);
	}

	/**
	 * 
	 * @param userId
	 * @param username
	 * @param request
	 * @param cartItems
	 * @return newOrderId
	 */
	private OrderDto createOrder(Long userId, String username, CheckOutRequest request, List<CartItemDto> cartItems) {
		OrderEntity order = new OrderEntity();
		order.setOrderNumber(generateOrderNumber());
		order.setUserId(userId);
		order.setUsername(username);
		order.setStatus(OrderStatus.PENDING.toString());
		order.setShippingAddress(request.getShippingAddress());
		order.setPaymentMethod(request.getPaymentMethod());
		order.setTotalAmount(calculateTotal(cartItems));

		OrderEntity savedOrder = orderRepository.save(order);
		
		List<OrderItemEntity> orderItems = cartItems.stream()
				.map(item -> convertToOrderItemEntity(item, savedOrder.getOrderId())).collect(Collectors.toList());
		orderItemRepository.saveAll(orderItems);
		return convertToFullDTO(savedOrder, orderItems);
	}

	/**
	 * Method to clear cart once order is placed.
	 * @param userId
	 */
	@Async
	private void clearCart(Long userId) {
		try {
			cartService.clearCart(userId);
			log.info("Cart cleared for user: {}", userId);
		} catch (Exception e) {
			log.error("Failed to clear cart for user: {}", userId, e);
			// Don't throw exception - order is already created
		}
	}

	/**
	 * 
	 * @return Unique Order Number
	 */
	private String generateOrderNumber() {
		return "ORD-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
	}

	/**
	 * 
	 * @param items
	 * @return totalOrderCost
	 */
	private BigDecimal calculateTotal(List<CartItemDto> items) {
		return items.stream().map(CartItemDto::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	/**
	 * 
	 * @param item
	 * @param orderId
	 * @return orderItemEntity
	 */
	private OrderItemEntity convertToOrderItemEntity(CartItemDto item, Long orderId) {
		OrderItemEntity orderItem = new OrderItemEntity();
		orderItem.setOrderId(orderId);
		orderItem.setProductId(item.getProductId());
		orderItem.setProductName(item.getProductName());
		orderItem.setQuantity(item.getQuantity());
		orderItem.setUnitPrice(item.getUnitPrice());
		orderItem.setTotalPrice(item.getTotalPrice());
		return orderItem;
	}

	/**
	 * 
	 * @param entity
	 * @return orderDto
	 */
	private OrderDto convertToBasicDTO(OrderEntity entity) {
		OrderDto dto = new OrderDto();
		dto.setOrderId(entity.getOrderId());
		dto.setOrderNumber(entity.getOrderNumber());
		dto.setUserId(entity.getUserId());
		dto.setUsername(entity.getUsername());
		dto.setStatus(OrderStatus.valueOf(entity.getStatus()));
		dto.setTotalAmount(entity.getTotalAmount());
		dto.setShippingAddress(entity.getShippingAddress());
		dto.setPaymentMethod(entity.getPaymentMethod());
		dto.setPaymentStatus(entity.getPaymentStatus());
		dto.setCreatedDate(entity.getCreatedDate());
		return dto;
	}

	/**
	 * 
	 * @param entity
	 * @param items
	 * @return complete order details
	 */
	private OrderDto convertToFullDTO(OrderEntity entity, List<OrderItemEntity> items) {
		OrderDto dto = convertToBasicDTO(entity);
		List<OrderItemDto> itemDTOs = items.stream().map(this::convertToItemDTO).collect(Collectors.toList());
		dto.setItems(itemDTOs);
		return dto;
	}

	/**
	 * 
	 * @param entity
	 * @return itemDto
	 */
	private OrderItemDto convertToItemDTO(OrderItemEntity entity) {
		return new OrderItemDto(entity.getProductId(), entity.getProductName(), entity.getQuantity(),
				entity.getUnitPrice(), entity.getTotalPrice());
	}
	
	private void stockCheck(List<CartItemDto> cartItems) {
	    log.info("stockCheck for Products start");
	    List<Long> productIds = cartItems.stream().map(CartItemDto::getProductId).collect(Collectors.toList());
	    
	    ProductIdRequestDto request = new ProductIdRequestDto();
	    request.setProductIds(productIds);
	    List<ProductDto> productStockList = productService.getProductsStock(request);
	    
	    for (CartItemDto cartItem : cartItems) {
	        ProductDto productStock = productStockList.stream().filter(p -> p.getProductId().equals(cartItem.getProductId()))
	                .findFirst()
	                .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));
	        
	        if (productStock.getStockQuantity() == null || productStock.getStockQuantity() < cartItem.getQuantity()) {
	            throw new RuntimeException("Insufficient stock for product: " + productStock.getName() + 
	                                     ". Available: " + productStock.getStockQuantity() + 
	                                     ", Requested: " + cartItem.getQuantity());
	        }
	        
	        if (productStock.getStockQuantity() <= 0) {
	            throw new RuntimeException("Product out of stock: " + productStock.getName());
	        }
	    }
	    
	    log.info("Stock validation passed for all products");
	}
}
	
	
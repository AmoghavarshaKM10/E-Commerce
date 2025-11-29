package com.ecommerce.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;//ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDto {
	
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private String username;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime createdDate;
    private List<OrderItemDto> items = new ArrayList<>();
    private String paymentApprovedUrl;
}




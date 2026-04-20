package com.ecommerce.order_service.dto.response;

import com.ecommerce.order_service.dto.OrderLineItemsDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String orderStatus;
    private List<OrderLineItemsDto> orderLineItems;
    private LocalDateTime createdAt;
}
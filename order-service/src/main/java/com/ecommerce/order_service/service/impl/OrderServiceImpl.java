package com.ecommerce.order_service.service.impl;

import com.ecommerce.order_service.dto.OrderLineItemsDto;
import com.ecommerce.order_service.dto.request.OrderRequest;
import com.ecommerce.order_service.dto.response.OrderResponse;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.entity.OrderLineItems;
import com.ecommerce.order_service.exception.ResourceNotFoundException;
import com.ecommerce.order_service.external.client.ProductClient;
import com.ecommerce.order_service.external.client.UserClient;
import com.ecommerce.order_service.external.response.ProductDto;
import com.ecommerce.order_service.repository.OrderRepository;
import com.ecommerce.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final UserClient userClient;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        log.info("Placing order for user ID: {}", orderRequest.getUserId());

        // verify user exists in User Service
        log.info("Verifying user with ID: {}", orderRequest.getUserId());
        userClient.getUserById(orderRequest.getUserId());

        // process each order line item
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream()
                .map(itemDto -> {
                    log.info("Verifying product and checking stock for SKU: {}", itemDto.getSku());

                    // fetch product from Product Service
                    ProductDto product = productClient.getProductBySku(itemDto.getSku());

                    // overriding with the DB price.
                    itemDto.setPrice(product.getPrice());

                    // deduct stock in Product Service
                    productClient.reduceStock(itemDto.getSku(), itemDto.getQuantity());

                    return mapToEntity(itemDto);
                })
                .collect(Collectors.toList());

        // save the Order
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .userId(orderRequest.getUserId())
                .orderLineItemsList(orderLineItems)
                .orderStatus("PLACED")
                .build();

        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully with Order Number: {}", savedOrder.getOrderNumber());

        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
        return mapToResponse(order);
    }

    private OrderLineItems mapToEntity(OrderLineItemsDto dto) {
        return OrderLineItems.builder()
                .sku(dto.getSku())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .build();
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderLineItemsDto> itemsDtos = order.getOrderLineItemsList().stream()
                .map(item -> {
                    OrderLineItemsDto dto = new OrderLineItemsDto();
                    dto.setSku(item.getSku());
                    dto.setPrice(item.getPrice());
                    dto.setQuantity(item.getQuantity());
                    return dto;
                }).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .orderStatus(order.getOrderStatus())
                .orderLineItems(itemsDtos)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
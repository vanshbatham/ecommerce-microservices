package com.ecommerce.order_service.dto.request;

import com.ecommerce.order_service.dto.OrderLineItemsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderLineItemsDto> orderLineItemsDtoList;
}
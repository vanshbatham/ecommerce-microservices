package com.ecommerce.order_service.event;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPlacedEvent {
    // sending the minimum data the downstream services need.
    // the Payment Service only needs to know who to charge, what order to link it to, and how much.
    private String orderNumber;
    private Long userId;
}
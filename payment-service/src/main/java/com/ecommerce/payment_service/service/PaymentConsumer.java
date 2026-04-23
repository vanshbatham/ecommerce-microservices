package com.ecommerce.payment_service.service;

import com.ecommerce.payment_service.entity.Payment;
import com.ecommerce.payment_service.event.OrderPlacedEvent;
import com.ecommerce.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentRepository paymentRepository;

    // this annotation tells Spring to start a background thread that constantly polls this topic
    @KafkaListener(topics = "order-placed-topic", groupId = "payment-group")
    @Transactional
    public void consumeOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for Order Number: {}", event.getOrderNumber());

        // IDEMPOTENCY CHECK: Have we seen this order before?
        if (paymentRepository.existsByOrderNumber(event.getOrderNumber())) {
            log.warn("Payment for Order Number {} already exists. Dropping duplicate message.", event.getOrderNumber());
            return;
        }

        log.info("Processing payment for User ID: {} and Order Number: {}", event.getUserId(), event.getOrderNumber());

        // in a real app, this might fail & we'd set status to "FAILED"
        String paymentStatus = "SUCCESS";

        Payment payment = Payment.builder()
                .orderNumber(event.getOrderNumber())
                .status(paymentStatus)
                .build();

        paymentRepository.save(payment);
        log.info("Payment processed successfully for Order Number: {}", event.getOrderNumber());
    }
}
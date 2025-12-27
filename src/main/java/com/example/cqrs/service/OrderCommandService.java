package com.example.cqrs.service;

import com.example.cqrs.model.OrderRecord;
import com.example.cqrs.projection.OrderProjector;
import com.example.cqrs.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class OrderCommandService {
    private final OrderRepository orderRepository;
    private final OrderProjector projector;

    public OrderCommandService(OrderRepository orderRepository, OrderProjector projector) {
        this.orderRepository = orderRepository;
        this.projector = projector;
    }

    @Transactional
    public String createOrder(String userId, Double amount) {
        String orderId = UUID.randomUUID().toString();
        OrderRecord order = new OrderRecord(
                orderId,
                userId,
                amount,
                "CREATED",
                Instant.now());

        // 1. Write to Command Model (PostgreSQL - Source of Truth)
        orderRepository.save(order);

        // 2. Trigger Projection (Sync to DynamoDB for high-speed reads)
        // In a real distributed system, this could be via Outbox Pattern or CDC
        projector.project(order);

        return orderId;
    }
}

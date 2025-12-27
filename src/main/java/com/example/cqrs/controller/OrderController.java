package com.example.cqrs.controller;

import com.example.cqrs.model.OrderQueryEntity;
import com.example.cqrs.service.OrderCommandService;
import com.example.cqrs.service.OrderQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderCommandService commandService;
    private final OrderQueryService queryService;

    public OrderController(OrderCommandService commandService, OrderQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public Map<String, String> createOrder(@RequestBody Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        Double amount = Double.valueOf(payload.get("amount").toString());
        String orderId = commandService.createOrder(userId, amount);
        return Map.of("orderId", orderId, "message", "Order created in PostgreSQL and projected to DynamoDB");
    }

    @GetMapping("/{userId}")
    public List<OrderQueryEntity> getOrders(@PathVariable String userId) {
        return queryService.getOrdersByUserId(userId);
    }
}

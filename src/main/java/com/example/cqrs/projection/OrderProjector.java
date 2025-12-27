package com.example.cqrs.projection;

import com.example.cqrs.model.OrderRecord;
import com.example.cqrs.model.OrderQueryEntity;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import org.springframework.stereotype.Component;

@Component
public class OrderProjector {
    private final DynamoDbTable<OrderQueryEntity> queryTable;

    public OrderProjector(DynamoDbEnhancedClient enhancedClient) {
        this.queryTable = enhancedClient.table("OrdersRead", TableSchema.fromBean(OrderQueryEntity.class));
    }

    public void project(OrderRecord order) {
        OrderQueryEntity readModel = OrderQueryEntity.builder()
                .userId(order.getUserId())
                .createdAt(order.getCreatedAt().toString())
                .orderId(order.getOrderId())
                .amount(order.getAmount())
                .status(order.getStatus())
                .build();

        queryTable.putItem(readModel);
    }
}

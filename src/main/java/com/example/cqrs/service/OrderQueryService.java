package com.example.cqrs.service;

import com.example.cqrs.model.OrderQueryEntity;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderQueryService {
    private final DynamoDbTable<OrderQueryEntity> queryTable;

    public OrderQueryService(DynamoDbEnhancedClient enhancedClient) {
        this.queryTable = enhancedClient.table("OrdersRead", TableSchema.fromBean(OrderQueryEntity.class));
    }

    public List<OrderQueryEntity> getOrdersByUserId(String userId) {
        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue(userId).build());

        return queryTable.query(queryConditional)
                .items()
                .stream()
                .collect(Collectors.toList());
    }
}

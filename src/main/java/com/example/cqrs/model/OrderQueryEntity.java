package com.example.cqrs.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class OrderQueryEntity {
    private String userId;
    private String createdAt;
    private String orderId;
    private Double amount;
    private String status;

    public OrderQueryEntity() {
    }

    public OrderQueryEntity(String userId, String createdAt, String orderId, Double amount, String status) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDbSortKey
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;
        private String createdAt;
        private String orderId;
        private Double amount;
        private String status;

        public Builder userId(String id) {
            this.userId = id;
            return this;
        }

        public Builder createdAt(String t) {
            this.createdAt = t;
            return this;
        }

        public Builder orderId(String id) {
            this.orderId = id;
            return this;
        }

        public Builder amount(Double a) {
            this.amount = a;
            return this;
        }

        public Builder status(String s) {
            this.status = s;
            return this;
        }

        public OrderQueryEntity build() {
            return new OrderQueryEntity(userId, createdAt, orderId, amount, status);
        }
    }
}

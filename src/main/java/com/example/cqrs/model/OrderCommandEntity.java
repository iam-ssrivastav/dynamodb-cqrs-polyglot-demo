package com.example.cqrs.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class OrderCommandEntity {
    private String orderId;
    private String userId;
    private Double amount;
    private String status;
    private String createdAt;

    public OrderCommandEntity() {
    }

    public OrderCommandEntity(String orderId, String userId, Double amount, String status, String createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    @DynamoDbPartitionKey
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String orderId;
        private String userId;
        private Double amount;
        private String status;
        private String createdAt;

        public Builder orderId(String id) {
            this.orderId = id;
            return this;
        }

        public Builder userId(String id) {
            this.userId = id;
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

        public Builder createdAt(String t) {
            this.createdAt = t;
            return this;
        }

        public OrderCommandEntity build() {
            return new OrderCommandEntity(orderId, userId, amount, status, createdAt);
        }
    }
}

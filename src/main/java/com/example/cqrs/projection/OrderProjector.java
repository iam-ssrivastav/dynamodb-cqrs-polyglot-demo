package com.example.cqrs.projection;

import com.example.cqrs.model.OrderRecord;
import com.example.cqrs.model.OrderQueryEntity;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import org.springframework.stereotype.Component;

@Component
public class OrderProjector {
    private final DynamoDbTable<OrderQueryEntity> queryTable;
    private final S3Client s3Client;
    private final SnsClient snsClient;

    public OrderProjector(DynamoDbEnhancedClient enhancedClient, S3Client s3Client, SnsClient snsClient) {
        this.queryTable = enhancedClient.table("OrdersRead", TableSchema.fromBean(OrderQueryEntity.class));
        this.s3Client = s3Client;
        this.snsClient = snsClient;
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

        // EXTRA AWS FEATURE: Store Receipt in S3
        uploadReceipt(order);

        // EXTRA AWS FEATURE: Send SNS Notification
        sendPaymentNotification(order);
    }

    private void uploadReceipt(OrderRecord order) {
        try {
            String content = "Receipt for Order: " + order.getOrderId() + "\nAmount: $" + order.getAmount();
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket("order-receipts")
                    .key("receipts/" + order.getOrderId() + ".txt")
                    .build(), RequestBody.fromString(content));
            System.out.println(" -> [S3] Receipt uploaded for " + order.getOrderId());
        } catch (Exception e) {
            System.out.println(" -> [S3] Receipt upload failed: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void sendPaymentNotification(OrderRecord order) {
        try {
            snsClient.publish(PublishRequest.builder()
                    .topicArn("arn:aws:sns:us-east-1:000000000000:order-notifications")
                    .message("New Order Created: " + order.getOrderId() + " for amount $" + order.getAmount())
                    .build());
            System.out.println(" -> [SNS] Notification sent for " + order.getOrderId());
        } catch (Exception e) {
            System.out.println(" -> [SNS] Notification failed.");
        }
    }
}

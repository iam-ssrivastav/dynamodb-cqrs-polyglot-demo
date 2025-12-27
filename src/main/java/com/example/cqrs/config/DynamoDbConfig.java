package com.example.cqrs.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.NotFoundException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    @Bean
    public DynamoDbClient dynamoDbClient() {
        DynamoDbClient client = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create("http://localhost:8000"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("fakeMyKeyId", "fakeSecretAccessKey")))
                .build();

        // Setup tables during bean creation to avoid complex cycles
        setupTables(client);
        return client;
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient client) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create("http://localhost:4566")) // Localstack default
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("fakeMyKeyId", "fakeSecretAccessKey")))
                .build();
    }

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create("http://localhost:4566"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("fakeMyKeyId", "fakeSecretAccessKey")))
                .build();
    }

    @PostConstruct
    public void setupAwsResources() {
        setupTables(dynamoDbClient());
        setupS3(s3Client());
        setupSns(snsClient());
    }

    private void setupS3(S3Client client) {
        try {
            client.createBucket(CreateBucketRequest.builder().bucket("order-receipts").build());
            System.out.println("S3 Bucket 'order-receipts' created.");
        } catch (Exception e) {
            System.out.println("S3 Bucket setup skipped (likely already exists or Localstack not fully ready).");
        }
    }

    private void setupSns(SnsClient client) {
        try {
            client.createTopic(CreateTopicRequest.builder().name("order-notifications").build());
            System.out.println("SNS Topic 'order-notifications' created.");
        } catch (Exception e) {
            System.out.println("SNS Topic setup skipped.");
        }
    }

    private void setupTables(DynamoDbClient client) {
        createTableIfNotExists(client, "OrdersRead", "userId", "createdAt");
    }

    private void createTableIfNotExists(DynamoDbClient client, String tableName, String partitionKey, String sortKey) {
        try {
            client.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
        } catch (ResourceNotFoundException e) {
            CreateTableRequest.Builder requestBuilder = CreateTableRequest.builder()
                    .tableName(tableName)
                    .provisionedThroughput(ProvisionedThroughput.builder()
                            .readCapacityUnits(5L)
                            .writeCapacityUnits(5L)
                            .build())
                    .attributeDefinitions(
                            AttributeDefinition.builder().attributeName(partitionKey)
                                    .attributeType(ScalarAttributeType.S).build(),
                            AttributeDefinition.builder().attributeName(sortKey).attributeType(ScalarAttributeType.S)
                                    .build())
                    .keySchema(
                            KeySchemaElement.builder().attributeName(partitionKey).keyType(KeyType.HASH).build(),
                            KeySchemaElement.builder().attributeName(sortKey).keyType(KeyType.RANGE).build());

            client.createTable(requestBuilder.build());
        }
    }
}

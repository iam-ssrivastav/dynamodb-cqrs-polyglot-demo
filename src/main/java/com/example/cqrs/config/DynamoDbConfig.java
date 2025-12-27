package com.example.cqrs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;

import jakarta.annotation.PostConstruct;
import java.net.URI;

@Configuration
public class DynamoDbConfig {

    private static final URI LOCALSTACK_URI = URI.create("http://localhost:4566");
    private static final URI DYNAMO_URI = URI.create("http://localhost:8000");

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(DYNAMO_URI)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("fakeMyKeyId", "fakeSecretAccessKey")))
                .build();
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
                .endpointOverride(LOCALSTACK_URI)
                .forcePathStyle(true)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("fakeMyKeyId", "fakeSecretAccessKey")))
                .build();
    }

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(LOCALSTACK_URI)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("fakeMyKeyId", "fakeSecretAccessKey")))
                .build();
    }

    @Bean
    public AwsInitializer awsInitializer(DynamoDbClient dynamoDbClient, S3Client s3Client, SnsClient snsClient) {
        return new AwsInitializer(dynamoDbClient, s3Client, snsClient);
    }

    public static class AwsInitializer {
        private final DynamoDbClient dynamoDbClient;
        private final S3Client s3Client;
        private final SnsClient snsClient;

        public AwsInitializer(DynamoDbClient dynamoDbClient, S3Client s3Client, SnsClient snsClient) {
            this.dynamoDbClient = dynamoDbClient;
            this.s3Client = s3Client;
            this.snsClient = snsClient;
        }

        @PostConstruct
        public void init() {
            setupTables();
            setupS3();
            setupSns();
        }

        private void setupTables() {
            try {
                dynamoDbClient.describeTable(DescribeTableRequest.builder().tableName("OrdersRead").build());
            } catch (ResourceNotFoundException e) {
                dynamoDbClient.createTable(CreateTableRequest.builder()
                        .tableName("OrdersRead")
                        .provisionedThroughput(
                                ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                        .attributeDefinitions(
                                AttributeDefinition.builder().attributeName("userId")
                                        .attributeType(ScalarAttributeType.S).build(),
                                AttributeDefinition.builder().attributeName("createdAt")
                                        .attributeType(ScalarAttributeType.S).build())
                        .keySchema(
                                KeySchemaElement.builder().attributeName("userId").keyType(KeyType.HASH).build(),
                                KeySchemaElement.builder().attributeName("createdAt").keyType(KeyType.RANGE).build())
                        .build());
            }
        }

        private void setupS3() {
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket("order-receipts").build());
            } catch (Exception ignored) {
            }
        }

        private void setupSns() {
            try {
                snsClient.createTopic(CreateTopicRequest.builder().name("order-notifications").build());
            } catch (Exception ignored) {
            }
        }
    }
}

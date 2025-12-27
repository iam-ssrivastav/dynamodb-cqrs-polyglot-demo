package com.example.cqrs.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

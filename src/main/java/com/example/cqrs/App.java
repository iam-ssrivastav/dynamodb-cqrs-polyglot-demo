package com.example.cqrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("\n🚀 Order Service (CQRS) is running!");
        System.out.println("Writes  -> PostgreSQL (Source of Truth)");
        System.out.println("Reads   -> DynamoDB (Materialized View)");
        System.out.println("API     -> http://localhost:8080/api/orders");
    }
}

# Scaling Beyond SQL: Building a Polyglot CQRS System with PostgreSQL and AWS DynamoDB

In modern distributed systems, the "one size fits all" database approach is rapidly becoming a bottleneck. When your application scales to millions of users, the same relational database that handles complex transactions can struggle with high-frequency read queries.

This is where **CQRS (Command Query Responsibility Segregation)** and **Polyglot Persistence** come into play.

---

## 🏗️ The Problem: The Single Database Bottleneck
Most applications start with a single database (e.g., PostgreSQL). While Postgres is phenomenal for ACID compliance, performance can degrade under heavy read-write contention or when performing complex joins on massive datasets for simple UI views.

## 🚀 The Solution: Polyglot CQRS
I developed a system that splits the application into two distinct lanes:
1.  **The Command Side (Writes)**: Handled by **PostgreSQL** to ensure every order is transactionally safe.
2.  **The Query Side (Reads)**: Handled by **AWS DynamoDB**, a NoSQL store optimized for sub-millisecond lookups.

---

## 🛠️ The Tech Stack
- **Source of Truth**: PostgreSQL (ACID compliant)
- **High-Access Read Model**: AWS DynamoDB
- **Blob Storage**: AWS S3 (for receipts)
- **Event Bus**: AWS SNS (for notifications)
- **Backend**: Spring Boot 3.x & Java 17

---

## 💻 Code Proof: The "Glue" (The Projector)
The magic happens in the `OrderProjector`, which acts as the bridge between both worlds. When a transaction completes in Postgres, the projector materializes that data into DynamoDB and triggers secondary AWS flows:

```java
// Logic that materializes the view across multiple AWS services
public void project(OrderRecord order) {
    // 1. Write optimized view to DynamoDB
    queryTable.putItem(OrderQueryEntity.from(order));

    // 2. Archive proof of purchase to S3
    uploadReceiptToS3(order);

    // 3. Notify downstream microservices via SNS
    publishRegistrationEvent(order);
}
```

## 📊 Architecture Diagram
![System Architecture](https://github.com/iam-ssrivastav/dynamodb-cqrs-polyglot-demo/blob/main/cqrs_architecture_diagram.png?raw=true)

---

## 🎯 Key Takeaways
By separating responsibilities, we achieve:
- **Independent Scaling**: Scale reads independently of writes.
- **Optimized Storage**: Use Relational for transactions and NoSQL for performance.
- **Resilience**: Even if the read model lags, the source of truth remains intact.

---

### 🔥 Want to see the full source code?
I've open-sourced the entire project including the Docker environment and Postman collections.

👉 **GitHub Repository**: [iam-ssrivastav/dynamodb-cqrs-polyglot-demo](https://github.com/iam-ssrivastav/dynamodb-cqrs-polyglot-demo)

---

**Found this helpful?**
✅ **Follow me** for more deep dives into System Design and AWS.
📩 **DM me on LinkedIn** for more details or if you want to collaborate on the code!

#AWS #SystemDesign #Java #SpringBoot #Microservices #CQRS #Development

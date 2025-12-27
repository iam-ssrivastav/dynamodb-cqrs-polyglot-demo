package com.example.cqrs.repository;

import com.example.cqrs.model.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderRecord, String> {
}

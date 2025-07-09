package com.walletservice.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

import java.time.LocalDateTime;

@Document
@Data
public class Transaction {
    @Id
    private String id;
    private String userId;
    private double amount;
    private LocalDateTime timestamp;
    private String type;

    public Transaction(String userId, double amount, String type) {
        this.userId = userId;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.type = type;
    }
}
package com.walletservice.domain.dtos;

import com.walletservice.domain.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class IntervalBalanceDTO {
    private String userId;
    private LocalDateTime start;
    private LocalDateTime end;
    private double initialBalance;
    private List<Transaction> transactions;
    private double finalBalance;
}

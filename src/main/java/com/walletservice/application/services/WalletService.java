package com.walletservice.application.services;

import com.walletservice.domain.dtos.IntervalBalanceDTO;
import com.walletservice.domain.dtos.WalletDTO;

import java.time.LocalDateTime;

public interface WalletService {
    WalletDTO createWallet(String userId);
    Double getBalance(String userId);
    IntervalBalanceDTO getBalanceBetween(String userId, LocalDateTime start, LocalDateTime end);
    Double deposit(String userId, double amount, String transactionType);
    Double withdraw(String userId, double amount);
    void transfer(String fromUserId, String toUserId, double amount);
}
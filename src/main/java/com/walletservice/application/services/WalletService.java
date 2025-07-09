package com.walletservice.application.services;

import com.walletservice.shared.dtos.IntervalBalanceDTO;
import com.walletservice.shared.dtos.WalletDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface WalletService {
    WalletDTO createWallet(String userId);
    Double getBalance(String userId);
    IntervalBalanceDTO getBalanceBetween(String userId, LocalDateTime start, LocalDateTime end);
    Double deposit(String userId, double amount);
    Double withdraw(String userId, double amount);
    void transfer(String fromUserId, String toUserId, double amount);
}
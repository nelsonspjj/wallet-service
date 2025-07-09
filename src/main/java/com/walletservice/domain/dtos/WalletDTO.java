package com.walletservice.domain.dtos;

import lombok.Data;

@Data
public class WalletDTO {
    private String userId;
    private double balance;

    public WalletDTO(String userId, double balance) {
        this.userId = userId;
        this.balance = balance;
    }
}
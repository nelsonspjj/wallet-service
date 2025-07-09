package com.walletservice.application.controllers;

import com.walletservice.application.services.WalletService;
import com.walletservice.domain.dtos.IntervalBalanceDTO;
import com.walletservice.domain.dtos.WalletDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;


    @PostMapping("/create")
    public WalletDTO createWallet(@RequestParam String userId) {
        return walletService.createWallet(userId);
    }

    @GetMapping("/balance")
    public Double getBalance(@RequestParam String userId) {
        return walletService.getBalance(userId);
    }

    @GetMapping("/balance/history")
    public IntervalBalanceDTO getBalanceBetweenDates(
            @RequestParam String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return walletService.getBalanceBetween(userId, start, end);
    }

    @PostMapping("/{userId}/deposit")
    public void deposit(@PathVariable String userId, @RequestParam double amount) {
        walletService.deposit(userId, amount);
    }

    @PostMapping("/{userId}/withdraw")
    public void withdraw(@PathVariable String userId, @RequestParam double amount) {
        walletService.withdraw(userId, amount);
    }

    @PostMapping("/transfer")
    public void transfer(@RequestParam String fromUserId, @RequestParam String toUserId, @RequestParam double amount) {
        walletService.transfer(fromUserId, toUserId, amount);
    }
}
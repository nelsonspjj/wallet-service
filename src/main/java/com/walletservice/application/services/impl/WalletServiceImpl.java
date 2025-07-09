package com.walletservice.application.services.impl;

import com.walletservice.application.services.WalletService;
import com.walletservice.domain.model.Transaction;
import com.walletservice.domain.model.Wallet;
import com.walletservice.infrastructure.config.properties.ConfluentProperties;
import com.walletservice.infrastructure.repository.TransactionRepository;
import com.walletservice.infrastructure.repository.WalletRepository;
import com.walletservice.domain.dtos.IntervalBalanceDTO;
import com.walletservice.domain.dtos.WalletDTO;
import com.walletservice.shared.exception.InsufficientFundsException;
import com.walletservice.shared.exception.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final ConfluentProperties confluentProperties;

    @Lazy
    @Autowired
    private WalletService self;

    @Override
    public WalletDTO createWallet(String userId) {
        Wallet wallet = new Wallet(userId);
        walletRepository.save(wallet);
        return new WalletDTO(userId, wallet.getBalance());
    }

    @Override
    @Cacheable(value = "wallets", key = "#userId")
    public Double getBalance(String userId) {
        log.info("Fetching balance for userId: {}", userId);
        Wallet wallet = walletRepository.findByUserId(userId);
        if (wallet == null) {
            throw new WalletNotFoundException("Wallet not found for userId: " + userId);
        }
        return wallet.getBalance();
    }

    @Override
    public IntervalBalanceDTO getBalanceBetween(String userId, LocalDateTime start, LocalDateTime end) {
        List<Transaction> before = transactionRepository.findByUserIdAndTimestampBefore(userId, start);
        double initial = 0.0;
        for (Transaction tx : before) {
            initial += "DEPOSIT".equalsIgnoreCase(tx.getType()) ? tx.getAmount() : -tx.getAmount();
        }

        List<Transaction> intervalTx = transactionRepository.findByUserIdAndTimestampBetween(userId, start, end);

        double finalBalance = initial;
        for (Transaction tx : intervalTx) {
            finalBalance += "DEPOSIT".equalsIgnoreCase(tx.getType()) ? tx.getAmount() : -tx.getAmount();
        }

        return new IntervalBalanceDTO(userId, start, end, initial, intervalTx, finalBalance);
    }


    @Override
    @CachePut(value = "wallets", key = "#userId")
    public Double deposit(String userId, double amount) {
        Wallet wallet = walletRepository.findByUserId(userId);
        wallet.deposit(amount);
        walletRepository.save(wallet);
        transactionRepository.save(new Transaction(userId, amount, "DEPOSIT"));
        kafkaTemplate.send(confluentProperties.getTopicNameWalletTransactions(), "Deposited " + amount + " to wallet of user " + userId);
        return wallet.getBalance();
    }

    @Override
    @CachePut(value = "wallets", key = "#userId")
    public Double withdraw(String userId, double amount) {
        Wallet wallet = walletRepository.findByUserId(userId);
        if (amount > wallet.getBalance()) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        wallet.withdraw(amount);
        walletRepository.save(wallet);
        transactionRepository.save(new Transaction(userId, amount, "WITHDRAW"));
        kafkaTemplate.send(confluentProperties.getTopicNameWalletTransactions(), "Withdrew " + amount + " from wallet of user " + userId);
        return wallet.getBalance();
    }

    @Override
    public void transfer(String fromUserId, String toUserId, double amount) {
        self.withdraw(fromUserId, amount);
        self.deposit(toUserId, amount);
        kafkaTemplate.send(confluentProperties.getTopicNameWalletTransactions(), "Transferred " + amount + " from user " + fromUserId + " to user " + toUserId);
    }
}
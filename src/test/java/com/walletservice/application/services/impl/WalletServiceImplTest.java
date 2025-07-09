package com.walletservice.application.services.impl;

import com.walletservice.domain.dtos.IntervalBalanceDTO;
import com.walletservice.domain.dtos.WalletDTO;
import com.walletservice.domain.model.Transaction;
import com.walletservice.domain.model.Wallet;
import com.walletservice.infrastructure.config.properties.ConfluentProperties;
import com.walletservice.infrastructure.repository.TransactionRepository;
import com.walletservice.infrastructure.repository.WalletRepository;
import com.walletservice.shared.exception.InsufficientFundsException;
import com.walletservice.shared.exception.WalletNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceImplTest {

    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ConfluentProperties confluentProperties;


    @BeforeEach
    public void setUp() {
        lenient().when(confluentProperties.getTopicNameWalletTransactions()).thenReturn("wallet-transactions");
    }

    @Test
    public void createWallet_ShouldCreateWallet_WhenUserIdIsProvided() {
        String userId = "testuser";

        WalletDTO result = walletService.createWallet(userId);

        assertEquals(userId, result.getUserId());
        assertEquals(0.0, result.getBalance());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    public void getBalance_ShouldReturnBalance_WhenWalletExists() {
        String userId = "testuser";
        Wallet wallet = new Wallet(userId);
        wallet.deposit(100.0);
        when(walletRepository.findByUserId(userId)).thenReturn(wallet);

        Double balance = walletService.getBalance(userId);

        assertEquals(100.0, balance);
        verify(walletRepository).findByUserId(userId);
    }

    @Test
    public void getBalance_ShouldThrowException_WhenWalletNotFound() {
        String userId = "testuser";
        when(walletRepository.findByUserId(userId)).thenReturn(null);

        WalletNotFoundException exception = assertThrows(WalletNotFoundException.class, () -> {
            walletService.getBalance(userId);
        });
        assertEquals("Wallet not found for userId: " + userId, exception.getMessage());
    }

    @Test
    public void deposit_ShouldUpdateBalance_WhenDepositIsMade() {
        String userId = "testuser";
        Wallet wallet = new Wallet(userId);
        wallet.deposit(100.0);
        when(walletRepository.findByUserId(userId)).thenReturn(wallet);
        when(walletRepository.save(wallet)).thenReturn(wallet);

        Double newBalance = walletService.deposit(userId, 50.0);

        assertEquals(150.0, newBalance);
        verify(transactionRepository).save(any(Transaction.class));
        verify(kafkaTemplate).send("wallet-transactions", "Deposited 50.0 to wallet of user " + userId);
    }

    @Test
    public void withdraw_ShouldUpdateBalance_WhenWithdrawalIsPossible() {
        String userId = "testuser";
        Wallet wallet = new Wallet(userId);
        wallet.deposit(100.0);
        when(walletRepository.findByUserId(userId)).thenReturn(wallet);
        when(walletRepository.save(wallet)).thenReturn(wallet);

        Double newBalance = walletService.withdraw(userId, 50.0);

        assertEquals(50.0, newBalance);
        verify(transactionRepository).save(any(Transaction.class));
        verify(kafkaTemplate).send("wallet-transactions", "Withdrew 50.0 from wallet of user " + userId);
    }

    @Test
    public void withdraw_ShouldThrowException_WhenInsufficientFunds() {
        String userId = "testuser";
        Wallet wallet = new Wallet(userId);
        wallet.deposit(30.0);
        when(walletRepository.findByUserId(userId)).thenReturn(wallet);

        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            walletService.withdraw(userId, 50.0);
        });
        assertEquals("Insufficient funds", exception.getMessage());
    }

    @Test
    public void getBalanceBetween_ShouldCalculateInitialAndFinalBalance_WhenTransactionsExist() {
        String userId = "testuser";
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();

        List<Transaction> beforeTransactions = new ArrayList<>();
        beforeTransactions.add(new Transaction(userId, 100.0, "DEPOSIT"));
        beforeTransactions.add(new Transaction(userId, 50.0, "WITHDRAW"));

        List<Transaction> intervalTransactions = new ArrayList<>();
        intervalTransactions.add(new Transaction(userId, 20.0, "DEPOSIT"));
        intervalTransactions.add(new Transaction(userId, 10.0, "WITHDRAW"));

        when(transactionRepository.findByUserIdAndTimestampBefore(userId, start))
                .thenReturn(beforeTransactions);
        when(transactionRepository.findByUserIdAndTimestampBetween(userId, start, end))
                .thenReturn(intervalTransactions);

        IntervalBalanceDTO result = walletService.getBalanceBetween(userId, start, end);

        assertEquals(userId, result.getUserId());
        assertEquals(start, result.getStart());
        assertEquals(end, result.getEnd());
        assertEquals(50.0, result.getInitialBalance());
        assertEquals(60.0, result.getFinalBalance());
        assertEquals(2, result.getTransactions().size());
        verify(transactionRepository).findByUserIdAndTimestampBefore(userId, start);
        verify(transactionRepository).findByUserIdAndTimestampBetween(userId, start, end);
    }

}
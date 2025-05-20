package com.example.service.impl;

import com.example.model.Account;
import com.example.model.Transaction;
import com.example.repository.AccountRepository;
import com.example.repository.TransactionRepository;
import com.example.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    @Override
    public void deposit(Long accountId, BigDecimal amount, String userId) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Счет не найден"));
        if (!account.getUserId().equals(userId)) {
            throw new AccessDeniedException("Вы не являетесь владельцем этого счета");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма пополнения должна быть положительной");
        }
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public void withdraw(Long accountId, BigDecimal amount, String userId) {
        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Счет не найден"));
        if (!account.getUserId().equals(userId)) {
            throw new AccessDeniedException("Вы не являетесь владельцем этого счета");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма снятия должна быть положительной");
        }
        BigDecimal balance = calculateBalance(accountId);
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств на счете");
        }
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount.negate());
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public BigDecimal calculateBalance(Long accountId) {
        BigDecimal sum = transactionRepository.sumAmountsByAccountId(accountId);
        return sum != null ? sum : BigDecimal.ZERO;
    }
}

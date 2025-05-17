package com.example.service;

import java.math.BigDecimal;


public interface TransactionService {
    void deposit(Long accountId, BigDecimal amount, String userId);
    void withdraw(Long accountId, BigDecimal amount, String userId);
    BigDecimal calculateBalance(Long accountId);
}

package com.example.controller;

import com.example.dto.AccountCreationRequest;
import com.example.model.Account;
import com.example.repository.AccountRepository;
import com.example.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class TransactionController {
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;

    public TransactionController(TransactionService transactionService, AccountRepository accountRepository) {
        this.transactionService = transactionService;
        this.accountRepository = accountRepository;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(
            @RequestBody AccountCreationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        Account account = new Account();
        account.setUserId(userId);
        account.setCurrency(request.getCurrency());
        accountRepository.save(account);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Void> deposit(
            @PathVariable Long accountId,
            @RequestBody BigDecimal amount,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        transactionService.deposit(accountId, amount, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<Void> withdraw(
            @PathVariable Long accountId,
            @RequestBody BigDecimal amount,
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        transactionService.withdraw(accountId, amount, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<Map<String, Object>> getBalance(
            @PathVariable Long accountId,
            @AuthenticationPrincipal Jwt jwt) throws AccessDeniedException {
        String userId = jwt.getSubject();
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Счет не найден"));
        if (!account.getUserId().equals(userId)) {
            throw new AccessDeniedException("Вы не являетесь владельцем этого счета");
        }
        BigDecimal balance = transactionService.calculateBalance(accountId);
        Map<String, Object> response = new HashMap<>();
        response.put("balance", balance);
        response.put("currency", account.getCurrency());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getUserAccounts(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        List<Account> accounts = accountRepository.findByUserId(userId);
        return ResponseEntity.ok(accounts);
    }

}

package com.expensetracker.controller;

import com.expensetracker.dto.AccountRequest;
import com.expensetracker.dto.AccountResponse;
import com.expensetracker.dto.AccountSummaryResponse;
import com.expensetracker.entity.User;
import com.expensetracker.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAllAccounts(user.getId()));
    }

    @GetMapping("/summary")
    public ResponseEntity<AccountSummaryResponse> getAccountSummary(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountService.getAccountSummary(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccountById(user.getId(), id));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AccountRequest request) {
        AccountResponse response = accountService.createAccount(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponse> updateAccount(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody AccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(user.getId(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        accountService.deleteAccount(user.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/balance")
    public ResponseEntity<AccountResponse> updateBalance(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(accountService.updateBalance(user.getId(), id, amount));
    }
}

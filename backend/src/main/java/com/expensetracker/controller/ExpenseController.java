package com.expensetracker.controller;

import com.expensetracker.dto.DailyExpenseResponse;
import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.dto.SummaryResponse;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.Expense.TransactionType;
import com.expensetracker.entity.User;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getExpenses(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID accountId) {

        Page<ExpenseResponse> expenses;

        if (type != null) {
            expenses = expenseService.getExpensesByType(user.getId(), type, page, size);
        } else if (categoryId != null) {
            expenses = expenseService.getExpensesByCategory(user.getId(), categoryId, page, size);
        } else if (accountId != null) {
            expenses = expenseService.getExpensesByAccount(user.getId(), accountId, page, size);
        } else {
            expenses = expenseService.getExpenses(user.getId(), page, size);
        }

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ExpenseResponse>> searchExpenses(
            @AuthenticationPrincipal User user,
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(expenseService.searchExpenses(user.getId(), query, page, size));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByDateRange(
            @AuthenticationPrincipal User user,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(expenseService.getExpensesByDateRange(user.getId(), startDate, endDate));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ExpenseResponse>> getRecentExpenses(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(expenseService.getRecentExpenses(user.getId()));
    }

    @GetMapping("/monthly-total")
    public double getMonthlyTotal(@AuthenticationPrincipal User user) {
        return expenseService.getMonthlyTotal(user);
    }

    @GetMapping("/summary")
    public SummaryResponse getSummary(@AuthenticationPrincipal User user) {
        return expenseService.getSummary(user);
    }

    @GetMapping("/daily")
    public List<DailyExpenseResponse> getDailyExpenses(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "7") int days) {
        return expenseService.getDailyExpenses(user, days);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(expenseService.getExpenseById(user.getId(), id));
    }

    @PostMapping
    public ResponseEntity<Expense> createExpense(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ExpenseRequest request) {
        Expense response = expenseService.create(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody ExpenseRequest request) {
        return expenseService.update(id, user, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        expenseService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}

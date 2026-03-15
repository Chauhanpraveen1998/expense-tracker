package com.expensetracker.controller;

import com.expensetracker.dto.DailyExpenseResponse;
import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.SummaryResponse;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public List<Expense> getAll(@AuthenticationPrincipal User user) {
        return expenseService.getAll(user);
    }

    @GetMapping("/recent")
    public List<Expense> getRecent(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "5") int limit) {
        return expenseService.getRecent(user, limit);
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

    @PostMapping
    public ResponseEntity<Expense> create(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseService.create(user, request));
    }

    @PutMapping("/{id}")
    public Expense update(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody ExpenseRequest request) {
        return expenseService.update(id, user, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id) {
        expenseService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}

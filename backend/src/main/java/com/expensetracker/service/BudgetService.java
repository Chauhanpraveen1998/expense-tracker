package com.expensetracker.service;

import com.expensetracker.dto.BudgetRequest;
import com.expensetracker.dto.BudgetStatusResponse;
import com.expensetracker.entity.Budget;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    public List<Budget> getAll(User user) {
        return budgetRepository.findByUserId(user.getId());
    }

    public Budget create(User user, BudgetRequest req) {
        Budget budget = Budget.builder()
                .amount(req.getAmount())
                .month(req.getMonth())
                .user(user)
                .category(resolveCategory(req.getCategoryId()))
                .build();
        return budgetRepository.save(budget);
    }

    public List<BudgetStatusResponse> getStatus(User user) {
        return budgetRepository.findByUserId(user.getId())
                .stream()
                .map(budget -> buildStatus(budget, user.getId()))
                .toList();
    }

    private BudgetStatusResponse buildStatus(Budget budget, UUID userId) {
        LocalDate start = budget.getMonth().withDayOfMonth(1);
        LocalDate end   = start.plusMonths(1);

        BigDecimal spent = budget.getCategory() != null
                ? expenseRepository.sumByUserCategoryAndDateRange(
                        userId, budget.getCategory().getId(), start, end)
                : expenseRepository.sumByUserAndDateRange(userId, start, end);

        double budgetAmt = budget.getAmount().doubleValue();
        double spentAmt  = spent.doubleValue();
        double pct       = budgetAmt > 0 ? (spentAmt / budgetAmt) * 100.0 : 0.0;

        return new BudgetStatusResponse(
                budget.getId(),
                budget.getCategory() != null ? budget.getCategory().getName() : null,
                budgetAmt,
                spentAmt,
                Math.round(pct * 100.0) / 100.0,
                spentAmt > budgetAmt
        );
    }

    private Category resolveCategory(UUID categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }
}

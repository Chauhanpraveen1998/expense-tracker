package com.expensetracker.service;

import com.expensetracker.dto.CategorySummary;
import com.expensetracker.dto.DailyExpenseResponse;
import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.SummaryResponse;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public List<Expense> getAll(User user) {
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(user.getId());
    }

    public List<Expense> getRecent(User user, int limit) {
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(
                user.getId(), PageRequest.of(0, Math.max(1, limit)));
    }

    public double getMonthlyTotal(User user) {
        LocalDate[] range = currentMonthRange();
        return expenseRepository
                .sumByUserAndDateRange(user.getId(), range[0], range[1])
                .doubleValue();
    }

    public List<DailyExpenseResponse> getDailyExpenses(User user, int days) {
        LocalDate startDate = LocalDate.now().minusDays(Math.max(1, days) - 1L);
        return expenseRepository.sumByDayFrom(user.getId(), startDate)
                .stream()
                .map(row -> new DailyExpenseResponse(
                        row[0].toString(),
                        ((java.math.BigDecimal) row[1]).doubleValue()))
                .toList();
    }

    public SummaryResponse getSummary(User user) {
        LocalDate[] thisMonth = currentMonthRange();
        LocalDate[] lastMonth = lastMonthRange();

        double totalThisMonth = expenseRepository
                .sumByUserAndDateRange(user.getId(), thisMonth[0], thisMonth[1])
                .doubleValue();

        double totalLastMonth = expenseRepository
                .sumByUserAndDateRange(user.getId(), lastMonth[0], lastMonth[1])
                .doubleValue();

        List<CategorySummary> byCategory = expenseRepository
                .sumByCategoryAndDateRange(user.getId(), thisMonth[0], thisMonth[1])
                .stream()
                .map(row -> new CategorySummary(
                        row[0] != null ? (String) row[0] : "Uncategorized",
                        ((BigDecimal) row[1]).doubleValue()))
                .toList();

        return new SummaryResponse(totalThisMonth, totalLastMonth, byCategory);
    }

    public Expense create(User user, ExpenseRequest req) {
        Expense expense = Expense.builder()
                .amount(req.getAmount())
                .description(req.getDescription())
                .expenseDate(req.getExpenseDate())
                .user(user)
                .category(resolveCategory(req.getCategoryId()))
                .build();
        return expenseRepository.save(expense);
    }

    public Expense update(UUID id, User user, ExpenseRequest req) {
        Expense expense = getOwnedExpense(id, user);
        expense.setAmount(req.getAmount());
        expense.setDescription(req.getDescription());
        expense.setExpenseDate(req.getExpenseDate());
        expense.setCategory(resolveCategory(req.getCategoryId()));
        return expenseRepository.save(expense);
    }

    public void delete(UUID id, User user) {
        expenseRepository.delete(getOwnedExpense(id, user));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Expense getOwnedExpense(UUID id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Expense not found: " + id);
        }
        return expense;
    }

    private Category resolveCategory(UUID categoryId) {
        if (categoryId == null) return null;
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    /** [firstDayOfCurrentMonth, firstDayOfNextMonth) */
    private static LocalDate[] currentMonthRange() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        return new LocalDate[]{start, start.plusMonths(1)};
    }

    /** [firstDayOfLastMonth, firstDayOfCurrentMonth) */
    private static LocalDate[] lastMonthRange() {
        LocalDate start = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        return new LocalDate[]{start, start.plusMonths(1)};
    }
}

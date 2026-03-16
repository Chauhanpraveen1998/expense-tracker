package com.expensetracker.service;

import com.expensetracker.dto.CategorySummary;
import com.expensetracker.dto.DailyExpenseResponse;
import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.dto.SummaryResponse;
import com.expensetracker.entity.Account;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.Expense.TransactionType;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.AccountRepository;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    public Page<ExpenseResponse> getExpenses(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return expenseRepository.findByUserIdOrderByDateDesc(userId, pageable)
                .map(ExpenseResponse::fromEntity);
    }

    public Page<ExpenseResponse> getExpensesByType(UUID userId, TransactionType type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return expenseRepository.findByUserIdAndTransactionTypeOrderByDateDesc(userId, type, pageable)
                .map(ExpenseResponse::fromEntity);
    }

    public Page<ExpenseResponse> getExpensesByCategory(UUID userId, UUID categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return expenseRepository.findByUserIdAndCategoryIdOrderByDateDesc(userId, categoryId, pageable)
                .map(ExpenseResponse::fromEntity);
    }

    public Page<ExpenseResponse> getExpensesByAccount(UUID userId, UUID accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return expenseRepository.findByUserIdAndAccountIdOrderByDateDesc(userId, accountId, pageable)
                .map(ExpenseResponse::fromEntity);
    }

    public List<ExpenseResponse> getExpensesByDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        return expenseRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate)
                .stream()
                .map(ExpenseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<ExpenseResponse> searchExpenses(UUID userId, String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return expenseRepository.searchByMerchant(userId, query, pageable)
                .map(ExpenseResponse::fromEntity);
    }

    public ExpenseResponse getExpenseById(UUID userId, UUID expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .filter(e -> e.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return ExpenseResponse.fromEntity(expense);
    }

    @Transactional
    public ExpenseResponse createExpense(UUID userId, ExpenseRequest request) {
        User user = expenseRepository.findById(userId)
                .map(Expense::getUser)
                .orElseGet(() -> {
                    throw new ResourceNotFoundException("User not found");
                });

        user = expenseRepository.findById(UUID.randomUUID()).isPresent() ? 
               expenseRepository.findById(UUID.randomUUID()).get().getUser() : null;
        
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        Account account = null;
        if (request.getAccountId() != null) {
            account = accountRepository.findById(request.getAccountId())
                    .filter(a -> a.getUser().getId().equals(userId))
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        }

        Expense expense = Expense.builder()
                .user(user)
                .account(account)
                .category(category)
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .merchantName(request.getMerchantName())
                .date(request.getDate())
                .description(request.getDescription())
                .note(request.getNote())
                .merchantLogoUrl(request.getMerchantLogoUrl())
                .isRecurring(request.getIsRecurring())
                .tags(request.getTags())
                .build();

        if (account != null) {
            updateAccountBalance(account, request.getAmount(), request.getTransactionType(), true);
        }

        Expense savedExpense = expenseRepository.save(expense);
        return ExpenseResponse.fromEntity(savedExpense);
    }

    @Transactional
    public ExpenseResponse updateExpense(UUID userId, UUID expenseId, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(expenseId)
                .filter(e -> e.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (expense.getAccount() != null) {
            updateAccountBalance(expense.getAccount(), expense.getAmount(), expense.getTransactionType(), false);
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        Account account = null;
        if (request.getAccountId() != null) {
            account = accountRepository.findById(request.getAccountId())
                    .filter(a -> a.getUser().getId().equals(userId))
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        }

        expense.setAccount(account);
        expense.setCategory(category);
        expense.setAmount(request.getAmount());
        expense.setTransactionType(request.getTransactionType());
        expense.setMerchantName(request.getMerchantName());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setNote(request.getNote());
        expense.setMerchantLogoUrl(request.getMerchantLogoUrl());
        expense.setIsRecurring(request.getIsRecurring());
        expense.setTags(request.getTags());

        if (account != null) {
            updateAccountBalance(account, request.getAmount(), request.getTransactionType(), true);
        }

        Expense updatedExpense = expenseRepository.save(expense);
        return ExpenseResponse.fromEntity(updatedExpense);
    }

    @Transactional
    public void deleteExpense(UUID userId, UUID expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .filter(e -> e.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (expense.getAccount() != null) {
            updateAccountBalance(expense.getAccount(), expense.getAmount(), expense.getTransactionType(), false);
        }

        expenseRepository.delete(expense);
    }

    public List<ExpenseResponse> getRecentExpenses(UUID userId) {
        return expenseRepository.findTop5ByUserIdOrderByDateDesc(userId)
                .stream()
                .map(ExpenseResponse::fromEntity)
                .collect(Collectors.toList());
    }

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
        Category category = null;
        if (req.getCategoryId() != null) {
            category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()));
        }

        Expense expense = Expense.builder()
                .user(user)
                .account(null)
                .category(category)
                .amount(req.getAmount())
                .transactionType(req.getTransactionType() != null ? req.getTransactionType() : TransactionType.EXPENSE)
                .merchantName(req.getMerchantName() != null ? req.getMerchantName() : (req.getDescription() != null ? req.getDescription() : "Unknown"))
                .date(req.getDate() != null ? req.getDate() : LocalDateTime.now())
                .description(req.getDescription())
                .note(req.getNote())
                .merchantLogoUrl(req.getMerchantLogoUrl())
                .isRecurring(req.getIsRecurring())
                .tags(req.getTags())
                .build();
        return expenseRepository.save(expense);
    }

    public Expense update(UUID id, User user, ExpenseRequest req) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Expense not found: " + id);
        }

        Category category = null;
        if (req.getCategoryId() != null) {
            category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()));
        }

        expense.setAmount(req.getAmount());
        expense.setDescription(req.getDescription());
        expense.setDate(req.getDate() != null ? req.getDate() : LocalDateTime.now());
        expense.setCategory(category);
        expense.setTransactionType(req.getTransactionType() != null ? req.getTransactionType() : TransactionType.EXPENSE);
        expense.setMerchantName(req.getMerchantName() != null ? req.getMerchantName() : (req.getDescription() != null ? req.getDescription() : "Unknown"));
        expense.setNote(req.getNote());
        expense.setMerchantLogoUrl(req.getMerchantLogoUrl());
        expense.setIsRecurring(req.getIsRecurring());
        expense.setTags(req.getTags());

        return expenseRepository.save(expense);
    }

    public void delete(UUID id, User user) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        
        if (!expense.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Expense not found: " + id);
        }
        
        expenseRepository.delete(expense);
    }

    private void updateAccountBalance(Account account, BigDecimal amount, TransactionType type, boolean isAdd) {
        BigDecimal currentBalance = account.getBalance();
        BigDecimal change = amount;

        if (type == TransactionType.EXPENSE) {
            change = isAdd ? change.negate() : change;
        } else {
            change = isAdd ? change : change.negate();
        }

        account.setBalance(currentBalance.add(change));
        accountRepository.save(account);
    }

    private static LocalDate[] currentMonthRange() {
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        return new LocalDate[]{start, start.plusMonths(1)};
    }

    private static LocalDate[] lastMonthRange() {
        LocalDate start = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        return new LocalDate[]{start, start.plusMonths(1)};
    }
}

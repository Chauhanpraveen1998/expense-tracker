package com.expensetracker.service;

import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.dto.SmsTransactionRequest;
import com.expensetracker.dto.SmsTransactionResponse;
import com.expensetracker.entity.Account;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.AccountRepository;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsSyncService {

    private final ExpenseRepository expenseRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private static final Map<String, String> MERCHANT_CATEGORY_MAP = new HashMap<>();

    static {
        MERCHANT_CATEGORY_MAP.put("swiggy", "Food & Dining");
        MERCHANT_CATEGORY_MAP.put("zomato", "Food & Dining");
        MERCHANT_CATEGORY_MAP.put("dominos", "Food & Dining");
        MERCHANT_CATEGORY_MAP.put("mcdonalds", "Food & Dining");
        MERCHANT_CATEGORY_MAP.put("kfc", "Food & Dining");
        MERCHANT_CATEGORY_MAP.put("starbucks", "Food & Dining");
        MERCHANT_CATEGORY_MAP.put("cafe", "Food & Dining");
        MERCHANT_CATEGORY_MAP.put("restaurant", "Food & Dining");

        MERCHANT_CATEGORY_MAP.put("uber", "Transport");
        MERCHANT_CATEGORY_MAP.put("ola", "Transport");
        MERCHANT_CATEGORY_MAP.put("rapido", "Transport");
        MERCHANT_CATEGORY_MAP.put("metro", "Transport");
        MERCHANT_CATEGORY_MAP.put("irctc", "Transport");

        MERCHANT_CATEGORY_MAP.put("amazon", "Shopping");
        MERCHANT_CATEGORY_MAP.put("flipkart", "Shopping");
        MERCHANT_CATEGORY_MAP.put("myntra", "Shopping");
        MERCHANT_CATEGORY_MAP.put("ajio", "Shopping");

        MERCHANT_CATEGORY_MAP.put("bigbasket", "Groceries");
        MERCHANT_CATEGORY_MAP.put("blinkit", "Groceries");
        MERCHANT_CATEGORY_MAP.put("zepto", "Groceries");
        MERCHANT_CATEGORY_MAP.put("dmart", "Groceries");

        MERCHANT_CATEGORY_MAP.put("netflix", "Entertainment");
        MERCHANT_CATEGORY_MAP.put("spotify", "Entertainment");
        MERCHANT_CATEGORY_MAP.put("hotstar", "Entertainment");
        MERCHANT_CATEGORY_MAP.put("prime", "Entertainment");

        MERCHANT_CATEGORY_MAP.put("electricity", "Bills & Utilities");
        MERCHANT_CATEGORY_MAP.put("airtel", "Bills & Utilities");
        MERCHANT_CATEGORY_MAP.put("jio", "Bills & Utilities");
        MERCHANT_CATEGORY_MAP.put("vodafone", "Bills & Utilities");

        MERCHANT_CATEGORY_MAP.put("petrol", "Fuel");
        MERCHANT_CATEGORY_MAP.put("indian oil", "Fuel");
        MERCHANT_CATEGORY_MAP.put("hp", "Fuel");
        MERCHANT_CATEGORY_MAP.put("shell", "Fuel");

        MERCHANT_CATEGORY_MAP.put("pharmacy", "Health");
        MERCHANT_CATEGORY_MAP.put("apollo", "Health");
        MERCHANT_CATEGORY_MAP.put("medplus", "Health");
        MERCHANT_CATEGORY_MAP.put("netmeds", "Health");
    }

    @Transactional
    public SmsTransactionResponse processSmsTransaction(UUID userId, SmsTransactionRequest request) {
        if (expenseRepository.existsBySmsHash(request.getSmsHash())) {
            log.info("Duplicate SMS transaction detected: {}", request.getSmsHash());
            return SmsTransactionResponse.builder()
                    .success(false)
                    .isDuplicate(true)
                    .message("Transaction already exists")
                    .build();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Account account = null;
        if (request.getAccountLastFour() != null && !request.getAccountLastFour().isEmpty()) {
            List<Account> accounts = accountRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
            account = accounts.stream()
                    .filter(a -> request.getAccountLastFour().equals(a.getLastFourDigits()))
                    .findFirst()
                    .orElse(null);
        } else if (request.getAccountId() != null) {
            account = accountRepository.findById(request.getAccountId()).orElse(null);
        }

        Category category = categorizeTransaction(request.getMerchantName(), request.getCategoryName());

        Expense expense = Expense.builder()
                .user(user)
                .account(account)
                .category(category)
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .merchantName(request.getMerchantName())
                .date(request.getDateTime() != null ? request.getDateTime() : LocalDateTime.now())
                .description("Auto-detected from SMS")
                .smsHash(request.getSmsHash())
                .tags(List.of("auto-detected", "sms"))
                .build();

        Expense savedExpense = expenseRepository.save(expense);

        if (account != null) {
            updateAccountBalance(account, request);
        }

        log.info("SMS transaction saved: {} - {} - ₹{}", 
                savedExpense.getId(), savedExpense.getMerchantName(), savedExpense.getAmount());

        return SmsTransactionResponse.builder()
                .success(true)
                .isDuplicate(false)
                .message("Transaction created successfully")
                .transaction(ExpenseResponse.fromEntity(savedExpense))
                .build();
    }

    @Transactional
    public List<SmsTransactionResponse> processBatchSmsTransactions(UUID userId, List<SmsTransactionRequest> requests) {
        List<SmsTransactionResponse> responses = new ArrayList<>();

        for (SmsTransactionRequest request : requests) {
            try {
                SmsTransactionResponse response = processSmsTransaction(userId, request);
                responses.add(response);
            } catch (Exception e) {
                log.error("Error processing SMS transaction: {}", e.getMessage());
                responses.add(SmsTransactionResponse.builder()
                        .success(false)
                        .isDuplicate(false)
                        .message("Error: " + e.getMessage())
                        .build());
            }
        }

        return responses;
    }

    private Category categorizeTransaction(String merchantName, String providedCategoryName) {
        if (providedCategoryName != null && !providedCategoryName.isEmpty()) {
            Optional<Category> category = categoryRepository.findByNameIgnoreCase(providedCategoryName);
            if (category.isPresent()) {
                return category.get();
            }
        }

        String lowerMerchant = merchantName.toLowerCase();

        for (Map.Entry<String, String> entry : MERCHANT_CATEGORY_MAP.entrySet()) {
            if (lowerMerchant.contains(entry.getKey())) {
                Optional<Category> category = categoryRepository.findByNameIgnoreCase(entry.getValue());
                if (category.isPresent()) {
                    return category.get();
                }
            }
        }

        return categoryRepository.findByNameIgnoreCase("Other").orElse(null);
    }

    private void updateAccountBalance(Account account, SmsTransactionRequest request) {
        if (request.getTransactionType() == Expense.TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(request.getAmount()));
        } else {
            account.setBalance(account.getBalance().add(request.getAmount()));
        }
        accountRepository.save(account);
    }
}

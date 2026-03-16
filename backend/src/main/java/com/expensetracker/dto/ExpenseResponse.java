package com.expensetracker.dto;

import com.expensetracker.entity.Expense;
import com.expensetracker.entity.Expense.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    private UUID id;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String merchantName;
    private LocalDateTime date;
    private String description;
    private String note;
    private String merchantLogoUrl;
    private Boolean isRecurring;
    private List<String> tags;
    
    private UUID categoryId;
    private String categoryName;
    
    private UUID accountId;
    private String accountName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ExpenseResponse fromEntity(Expense expense) {
        ExpenseResponseBuilder builder = ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .transactionType(expense.getTransactionType())
                .merchantName(expense.getMerchantName())
                .date(expense.getDate())
                .description(expense.getDescription())
                .note(expense.getNote())
                .merchantLogoUrl(expense.getMerchantLogoUrl())
                .isRecurring(expense.getIsRecurring())
                .tags(expense.getTags())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt());

        if (expense.getCategory() != null) {
            builder.categoryId(expense.getCategory().getId())
                   .categoryName(expense.getCategory().getName());
        }

        if (expense.getAccount() != null) {
            builder.accountId(expense.getAccount().getId())
                   .accountName(expense.getAccount().getName());
        }

        return builder.build();
    }
}

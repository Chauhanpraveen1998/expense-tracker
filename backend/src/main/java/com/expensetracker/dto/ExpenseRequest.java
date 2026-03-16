package com.expensetracker.dto;

import com.expensetracker.entity.Expense.TransactionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType = TransactionType.EXPENSE;

    @NotBlank(message = "Merchant name is required")
    @Size(max = 200, message = "Merchant name must be less than 200 characters")
    private String merchantName;

    private UUID categoryId;

    private UUID accountId;

    @NotNull(message = "Date is required")
    private LocalDateTime date;

    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @Size(max = 500, message = "Note must be less than 500 characters")
    private String note;

    private String merchantLogoUrl;

    private Boolean isRecurring = false;

    @Builder.Default
    private List<String> tags = new ArrayList<>();
}

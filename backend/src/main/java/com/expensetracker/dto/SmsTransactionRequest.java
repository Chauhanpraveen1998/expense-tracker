package com.expensetracker.dto;

import com.expensetracker.entity.Expense.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsTransactionRequest {

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @NotBlank(message = "Merchant name is required")
    private String merchantName;

    private String categoryName;

    private UUID accountId;

    private String accountLastFour;

    private LocalDateTime dateTime;

    private String referenceNumber;

    private String smsHash;

    private String rawMessage;
}

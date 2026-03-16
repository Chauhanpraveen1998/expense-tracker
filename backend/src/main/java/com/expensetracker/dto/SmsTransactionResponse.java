package com.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsTransactionResponse {

    private boolean success;
    private boolean isDuplicate;
    private String message;
    private ExpenseResponse transaction;
}

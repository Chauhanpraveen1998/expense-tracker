package com.expensetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BudgetRequest {

    @NotNull @DecimalMin("0.01")
    private BigDecimal amount;

    /** Provide the first day of the target month, e.g. 2024-03-01 */
    @NotNull
    private LocalDate month;

    private UUID categoryId;
}
